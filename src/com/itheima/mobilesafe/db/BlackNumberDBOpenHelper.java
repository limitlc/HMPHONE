package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}
	
	//数据库第一次创建的时候调用的方法。 适合做数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建数据库的表结构  主键_id 自增长  number黑名单号码  mode拦截模式  1电话拦截 2短信拦截 3全部拦截。
		db.execSQL("create table blacknumber (_id integer primary key autoincrement , number varchar(20), mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
