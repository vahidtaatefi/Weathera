package com.taatefi.weather.Utilities;


import android.content.Context;
import com.taatefi.weather.R;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {
  //  private static final String open_weather_map_api="https://openweathermap.org/data/2.5/weather?q=tehran&appid=b6907d289e10d714a6e88b30761fae22"

//    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?";
      private static final String OPEN_WEATHER_MAP_API = "https://openweathermap.org/data/2.5/weather?";

    public static JSONObject getTodayForecast(Context context, String city) {
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API + "q=" + city+"&appid=b6907d289e10d714a6e88b30761fae22" );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null) json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }

 //   private static final String OPEN_WEATHER_MAP_API_SEVEN_DAY = "http://api.openweathermap.org/data/2.5/forecast/daily?";
      private static final String OPEN_WEATHER_MAP_API_SEVEN_DAY = "https://openweathermap.org/data/2.5/forecast/daily?";

    public static JSONObject getFiveDayForecast(Context context, String city) {
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API_SEVEN_DAY + "q=" + city+"&appid=b6907d289e10d714a6e88b30761fae22");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null) json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
