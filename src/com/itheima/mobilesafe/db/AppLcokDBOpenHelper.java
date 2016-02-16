package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLcokDBOpenHelper extends SQLiteOpenHelper {

	public AppLcokDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	//数据库第一次创建的时候调用的方法。 适合做数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建数据库的表结构  主键_id 自增长  packname锁定应用程序的包名
		db.execSQL("create table applock (_id integer primary key autoincrement , packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
