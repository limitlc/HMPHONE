package com.itheima.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoKillService extends Service {
	private InnerScreenOffReceiver screenOffReceiver ;
	
	
	private class InnerScreenOffReceiver extends BroadcastReceiver{
		private static final String TAG = "InnerScreenOffReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"ÆÁÄ»±»Ëø¶¨ÁË¡£¡£¡£");
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for(RunningAppProcessInfo info: infos){
				am.killBackgroundProcesses(info.processName);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		screenOffReceiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenOffReceiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(screenOffReceiver);
		screenOffReceiver = null;
		super.onDestroy();
	}

}
