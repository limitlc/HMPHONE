package com.itheima.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;

/**
 * 代表的是整个应用程序
 * 使用他需要在清单文件里面配置
 * @author Administrator
 *
 */
public class MobileSafeApplication extends Application {
	/**
	 * 整个应用程序的初始化
	 * 在应用程序的其他对象创建之前就调用的方法
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		
	}
	
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//当产生了未捕获的异常
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("产生了一个未处理的异常，但是这个异常被哥给捕获了");
			try {
				File file = new File(Environment.getExternalStorageDirectory(),"error.log");
				PrintWriter pw = new PrintWriter(file);
				ex.printStackTrace(pw);
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//早死早超生
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
