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
 * ���ݿ���ɾ�Ĳ�Ĺ�����
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	/**
	 * ���췽����������ݿ�򿪰�����ĳ�ʼ��
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * ���һ������������
	 * @param number ����������
	 * @param mode ����ģʽ  1�绰���� 2�������� 3ȫ�����ء�
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
	 * ɾ��һ������������
	 * @param number ����������
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
	/**
	 * ����һ�����������������ģʽ
	 * @param number Ҫ�޸ĵĺ���������
	 * @param newmode �µ�����ģʽ
	 */
	public void update(String number, String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	
	/**
	 * ��ȡȫ���ĺ�����������Ϣ��
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
	 * ��ҳ��ȡ���ֵĺ�����������Ϣ��
	 * @param startIndex ��ѯ�Ŀ�ʼλ��
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
	 * ��ȡ���ݿ�һ���ж�������¼
	 * @return  int ����Ŀ����
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
	 * ��ѯ�����������Ƿ����
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
	 * ��ѯ���������������ģʽ
	 * @param number
	 * @return  null��������  1�绰 2���� 3ȫ��
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
