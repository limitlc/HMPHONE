package com.itheima.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.receiver.MyWidget;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	protected static final String TAG = "UpdateWidgetService";
	private InnerScreenOffReceiver offReceiver;
	private InnerScreenOnReceiver onReceiver;
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerScreenOffReceiver extends BroadcastReceiver{
		private static final String TAG = "InnerScreenOffReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"��Ļ�������ˡ�����");
			if(timer!=null&&task!=null){
				timer.cancel();
				task.cancel();
				timer = null;
				task = null;
			}
			
		}
	}
	private class InnerScreenOnReceiver extends BroadcastReceiver{
		private static final String TAG = "InnerScreenOffReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"��Ļ����������");
			if(timer==null&&task ==null){
				startWidgetUpdate();
			}
			
		}
	}
	
	@Override
	public void onCreate() {
		offReceiver = new InnerScreenOffReceiver();
		onReceiver = new InnerScreenOnReceiver();
		
		IntentFilter offfilter = new IntentFilter();
		offfilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(offReceiver, offfilter);
		
		IntentFilter onfilter = new IntentFilter();
		onfilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(onReceiver, onfilter);
		
		//��ȡ��widget�Ĺ�����
		awm = AppWidgetManager.getInstance(this);
		startWidgetUpdate();
		super.onCreate();
	}

	private void startWidgetUpdate() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG,"�ҿ�ʼ�ѵ��ˡ�����Ҫ���½���");
				//��������һ�����̵Ĳ����� ipc���á� inter process communication ���̼�ͨѶ
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				//ָ��Ҫ���µ����ĸ�widget
				ComponentName provider = new ComponentName(getApplicationContext(), MyWidget.class);
				views.setTextViewText(R.id.process_count, "�������н���������"+SystemInfoUtils.getRunningProcessCount(getApplicationContext()));
				views.setTextViewText(R.id.process_memory, "�����ڴ棺"+Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailRam(getApplicationContext())));
				//����һ��Ӧ�ó���ִ�е����ڵ���ͼ
				//һ�����ڵ���ͼ  ����һ���Զ���Ĺ㲥
				Intent i = new Intent();
				i.setAction("com.itheima.mobilesafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 5000);
	}
	
	@Override
	public void onDestroy() {
		if(timer!=null&&task!=null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
		
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		
		super.onDestroy();
	}
	
}
