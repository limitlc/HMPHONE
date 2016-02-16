package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

/**
 * 数据库增删改查的工具类
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	/**
	 * 构造方法中完成数据库打开帮助类的初始化
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * 添加一条黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式  1电话拦截 2短信拦截 3全部拦截。
	 */
	public void add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	/**
	 * 删除一条黑名单号码
	 * @param number 黑名单号码
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
	/**
	 * 更改一条黑名单号码的拦截模式
	 * @param number 要修改的黑名单号码
	 * @param newmode 新的拦截模式
	 */
	public void update(String number, String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	
	/**
	 * 获取全部的黑名单号码信息。
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"number","mode"}, null, null, null, null, null);
		List<BlackNumberInfo>  infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			infos.add(info);
		}
		cursor.close();
		db.close();
		return infos;
	}
	/**
	 * 分页获取部分的黑名单号码信息。
	 * @param startIndex 查询的开始位置
	 * @return
	 */
	public List<BlackNumberInfo> findPart(int startIndex){
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode  from blacknumber order by _id desc limit 20 offset ?", new String[]{String.valueOf(startIndex)});
		List<BlackNumberInfo>  infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			infos.add(info);
		}
		cursor.close();
		db.close();
		return infos;
	}
	/**
	 * 获取数据库一共有多少条记录
	 * @return  int 总条目个数
	 */
	public int getTotalCount(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber ",null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}
	/**
	 * 查询黑名单号码是否存在
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", null, "number=?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询黑名单号码的拦截模式
	 * @param number
	 * @return  null代表不存在  1电话 2短信 3全部
	 */
	public String findMode(String number){
		String mode = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
}
