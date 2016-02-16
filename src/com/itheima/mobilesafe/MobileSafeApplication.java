package com.itheima.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;

/**
 * �����������Ӧ�ó���
 * ʹ������Ҫ���嵥�ļ���������
 * @author Administrator
 *
 */
public class MobileSafeApplication extends Application {
	/**
	 * ����Ӧ�ó���ĳ�ʼ��
	 * ��Ӧ�ó�����������󴴽�֮ǰ�͵��õķ���
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		
	}
	
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//��������δ������쳣
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("������һ��δ������쳣����������쳣�����������");
			try {
				File file = new File(Environment.getExternalStorageDirectory(),"error.log");
				PrintWriter pw = new PrintWriter(file);
				ex.printStackTrace(pw);
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//�����糬��
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
