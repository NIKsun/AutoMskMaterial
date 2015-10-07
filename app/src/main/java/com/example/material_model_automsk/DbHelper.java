package com.example.material_model_automsk;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Никита on 06.10.2015.
 */
class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "main_db3", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favorites ("
                + "href text primary key,"
                + "message text," + "dateTime text,"
                + "image text);");
        Log.d("DB", "onCr");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }
}