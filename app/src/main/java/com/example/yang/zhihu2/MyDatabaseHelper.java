package com.example.yang.zhihu2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String User="create table userdata("
            + "id integer primary key autoincrement,"
            +"user_image text,"
            + "nickname text,"
            + "username text,"
            + "password text,"
            + "birth text,"
            + "call text,"
            + "sex text)";
    private static final String LoveNews="create table lovenews_data("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"news_id text,"
            +"name text,"
            +"description text,"
            +"thumbnail text)";

    private static final String LoveEssay="create table loveessay_data("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"title text,"
            +"essay_id text,"
            +"thumbnail text,"
            +"url,text)";


    MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User);
        db.execSQL(LoveNews);
        db.execSQL(LoveEssay);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists userdata") ;
        db.execSQL("drop table if exists lovenews_data");
        db.execSQL("drop table if exists loveessay_data");
        onCreate(db) ;
    }
}