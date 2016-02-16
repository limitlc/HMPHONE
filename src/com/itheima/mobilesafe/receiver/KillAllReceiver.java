package com.itheima.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KillAllReceiver extends BroadcastReceiver {

	private static final String TAG = "KillAllReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"接收到了自定义的广播，把所有的进程给咔嚓掉");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info:infos){
			am.killBackgroundProcesses(info.processName);
		}
	}
}
