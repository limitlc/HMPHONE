package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsTools {
	
	//B 工程师定义一个接口 。 暴露一些回调函数。
	
	public interface BackUpCallBack{
		/**
		 * 当短信备份前调用的方法
		 * @param total  总的短信个数
		 */
		public void beforeSmsBackup(int total);
		
		/**
		 * 短信备份中调用的方法 
		 * @param progress 当前备份的进度。
		 */
		public void onSmsBackup(int progress);
	}
	
	
	/**
	 * 备份短信
	 * @param context 上下文
	 * @param path 短信备份后文件的路径
	 * @param pb 进度条
	 * @param pd 进度条对话框
	 * 
	 */
	public static void backup(Context context,String path,BackUpCallBack backupCallback) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		
		//指定序列号器的参数
		File file = new File(path);
		FileOutputStream fos = new FileOutputStream(file);
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		backupCallback.beforeSmsBackup(cursor.getCount());
		//pd.setmax(cursor.getcount());
		int progress = 0;
		while(cursor.moveToNext()){
			serializer.startTag(null, "sms");
			serializer.startTag(null, "address");
			String address = cursor.getString(0);
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "date");
			String date = cursor.getString(1);
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.startTag(null, "type");
			String type = cursor.getString(2);
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "body");
			String body = cursor.getString(3);
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.endTag(null, "sms");
			Thread.sleep(1000);
			progress++;
			//pb.setProgress(progress);
			backupCallback.onSmsBackup(progress);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
}
