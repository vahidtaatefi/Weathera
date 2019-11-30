package com.taatefi.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.taatefi.weather.Utilities.Forecast;
import com.taatefi.weather.Utilities.ForecastAdapter;
import com.taatefi.weather.Utilities.RemoteFetch;
import com.taatefi.weather.Utilities.SetListViewHeight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {
    //  RelativeLayout curr=findViewById(R.id.rl);
    private TextView mTemp, mHumidity, mTempHigh, mTempLow, mName, mWeather, mWeatherIcon;
    private ListView mListViewForecast;
    private List<Forecast> arrayListForecast;
    private Handler handler;
    public SqliteHelper helper = new SqliteHelper(MainActivity.this, "WeatherCity", null, 1);

    ListView lstcity;//= (ListView) findViewById(R.id.lstcity);

    private DrawerLayout[] drawerLayout = new DrawerLayout[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/Weather-Fonts.ttf");
        Typeface robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        updateWeather("tehran");
        mListViewForecast = (ListView) findViewById(R.id.listView);
        mListViewForecast.setEnabled(false);
        mTemp = (TextView) findViewById(R.id.temp);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mTempHigh = (TextView) findViewById(R.id.tempHigh);
        mTempLow = (TextView) findViewById(R.id.tempLow);
        mName = (TextView) findViewById(R.id.name);
        mWeather = (TextView) findViewById(R.id.weather);
        mWeatherIcon = (TextView) findViewById(R.id.weatherIcon);
        mWeatherIcon.setTypeface(weatherFont);
        mTemp.setTypeface(robotoThin);
        mName.setTypeface(robotoLight);
        mWeather.setTypeface(robotoLight);
        handler = new Handler();
        arrayListForecast = new ArrayList<>();
        ImageButton navbar = findViewById(R.id.navbar);
        Button btnsearch = findViewById(R.id.btnsearch);
        final EditText edtsearch = findViewById(R.id.edtsearch);

        lstcity = (ListView) findViewById(R.id.lstcity);
        lstcity.setAdapter(setcit());

        lstcity.setOnItemClickListener(listclick);

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.insertCity((edtsearch.getText().toString()));
                updateWeather(edtsearch.getText().toString());
                drawerLayout[0] = findViewById(R.id.draw);
                hideSoftKeyboard(edtsearch);
                edtsearch.setText("");
                lstcity.setAdapter(setcit());
                drawerLayout[0].closeDrawer(GravityCompat.START);


                //     txtcity.setText(city);

            }
        });
        navbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   DrawerLayout drawerLayout = findViewById(R.id.draw);
                drawerLayout[0] = findViewById(R.id.draw);
                drawerLayout[0].openDrawer(GravityCompat.START);
            }
        });
    }
    public ArrayAdapter<String> setcit()

    {
        String[] ll = helper.getAllCity();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, ll);
        return adapter;
    }
    private AdapterView.OnItemClickListener listclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = (String) lstcity.getItemAtPosition(position);
            updateWeather(item);

            drawerLayout[0].closeDrawer(GravityCompat.START);
        }
    };

    public static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void updateWeather(final String city) {
        new Thread() {
            public void run() {
                final JSONObject jsonCurrent = RemoteFetch.getTodayForecast(MainActivity.this, city);
                final JSONObject jsonForecast = RemoteFetch.getFiveDayForecast(MainActivity.this, city);
                if (jsonCurrent == null && jsonForecast == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "GPS Not Enabled", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderCurrentWeather(jsonCurrent);
                            renderForecastWeather(jsonForecast);
                        }
                    });
                }
            }
        }.start();
    }

    public void getweathercurrent(String city) {
        String search = "https://api.weatherbit.io/v2.0/current?city=tehran&key=d72b21c280d84f6d88a6c251a23a2559";
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(search, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONObject weather = response.getJSONArray("data").getJSONObject(0);
                        String cityn = (weather.getString("city_name")) + "-" + (weather.getString("country_code"));// اسم شهر
                        String time = (weather.getString("ob_time"));//زمان
                        JSONObject weath = weather.getJSONObject("weather");
                        String icon = "https://www.weatherbit.io/static/img/icons/" + weath.getString("icon") + ".png";
                        String uv = (weather.getString("uv"));
                        String wind = (weather.getString("wind_spd"));// سرعت باد
                        String temp = weather.getString("temp");
                        Bundle bundle = new Bundle();
                        bundle.putString("cityn", cityn);
                        bundle.putString("time", time);
                        bundle.putString("icon", icon);
                        bundle.putString("uv", uv);
                        bundle.putString("wind", wind);
                        bundle.putString("temp", temp);
                        //PASS OVER THE BUNDLE TO OUR FRAGMENT
                        //       todayfrag myFragment = new todayfrag();
                        //     myFragment.setArguments(bundle);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }



                /*  @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        txtcit = findViewById(R.id.cityname);
                        JSONObject weather = response.getJSONArray("data").getJSONObject(0);
                        String wind = weather.getString("wind_spd");
                        txtcit.setText(weather.getString("city_name"));
                        //  cityn = weather.getString("city_name");
                        //  txtcity.setText(weather.getString("city_name"));
                        Log.d("speed", wind);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }*/


    }

    private void renderCurrentWeather(JSONObject json) {
        try {
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            mName.setText(json.getString("name"));
            mWeather.setText(weather.getString("description"));
            mTemp.setText(main.getLong("temp") + "" + (char) 0x00B0);
            mTempHigh.setText("MAX: " + main.getLong("temp_max") + "" + (char) 0x00B0);
            mTempLow.setText("MIN: " + main.getLong("temp_min") + "" + (char) 0x00B0);
            mHumidity.setText("Humidity " + main.getString("humidity") + "%");
            setWeatherIcon(weather.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000);
        } catch (JSONException e) {
            Log.e("CURRENT_JSON_ERROR", e.toString());
        }
    }

    private void renderForecastWeather(JSONObject json) {
        try {
            arrayListForecast.clear(); // clear list, prevent duplicates on refresh
            JSONArray list = json.getJSONArray("list");
            for (int i = 0; i < 6; i++) {
                JSONObject listItem = list.getJSONObject(i);
                JSONObject temp = listItem.getJSONObject("temp");
                JSONObject weather = listItem.getJSONArray("weather").getJSONObject(0);
                Forecast forecast = new Forecast();
                forecast.setHighTemp(String.valueOf(temp.getLong("max")));
                forecast.setLowTemp(String.valueOf(temp.getLong("min")));
                forecast.setWeather(weather.get("description").toString());
                forecast.setWeatherId(weather.get("id").toString());
                arrayListForecast.add(forecast);
            }
            ForecastAdapter testAdapter = new ForecastAdapter(this, 0, arrayListForecast);
            mListViewForecast.setAdapter(testAdapter);
            SetListViewHeight.setListViewHeight(mListViewForecast);
        } catch (JSONException e) {
            Log.e("FORECAST_JSON_ERROR", e.toString());
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        mWeatherIcon.setText(icon);
    }


}
