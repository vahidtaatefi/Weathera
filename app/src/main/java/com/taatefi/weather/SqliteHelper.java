package com.taatefi.weather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {
    String TABLE_NAME = "City";

    public SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "City TEXT"
                + ")";
        db.execSQL(CREATE_TABLE_QUERY);
    }

    public void insertCity(String City) {
        String GET_City_Name= "SELECT City FROM " + TABLE_NAME+" where City='"+City+"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(GET_City_Name, null);
        if(data.getCount()>0)
        {}
        else
            {
            String INSERT_STUDENT_QUERY = "INSERT INTO " + TABLE_NAME + "(City) VALUES("
                    + "'" + City + "'"
                    + ")";
            db.execSQL(INSERT_STUDENT_QUERY);
            db.close();
        }
    }

    public String[] getAllCity() {
        int i = 0;
          ;//= new String[];
        String GET_City_Name= "SELECT City FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(GET_City_Name, null);
        String result[]=new String[data.getCount()];
        while (data.moveToNext()) {
            result[i] = data.getString(0);
            i++;
        }
        db.close();
        return result;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
