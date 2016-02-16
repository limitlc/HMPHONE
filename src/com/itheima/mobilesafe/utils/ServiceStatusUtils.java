package com.itheima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * 检查一个服务是否处于运行状态
	 * @param context
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviename){
		ActivityManager  am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for(RunningServiceInfo info :infos){
			String classname = info.service.getClassName();
			if(serviename.equals(classname)){
				return true;
			}
		}
		return false;
	}
}
