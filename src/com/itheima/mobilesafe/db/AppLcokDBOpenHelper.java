package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLcokDBOpenHelper extends SQLiteOpenHelper {

	public AppLcokDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	//���ݿ��һ�δ�����ʱ����õķ����� �ʺ������ݿ��ṹ�ĳ�ʼ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		//�������ݿ�ı�ṹ  ����_id ������  packname����Ӧ�ó���İ���
		db.execSQL("create table applock (_id integer primary key autoincrement , packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
