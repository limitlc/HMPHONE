package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

/**
 * 系统信息的工具类
 * @author Administrator
 *
 */
public class SystemInfoUtils {

	/**
	 * 获取正在运行的进程的个数
	 * @return 进程数量
	 */
	public static int getRunningProcessCount(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	
	/**获取手机可用的内存信息 ram
	 * 
	 * @param context
	 * @return 单位是byte
	 */
	public static long getAvailRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem; //byte为单位的long类型的可用内存大小
	}
	
	/**获取手机总内存信息 ram
	 * 
	 * @param context
	 * @return 单位是byte
	 */
	public static long getTotalRam(Context context){
//      下面的api  totalmem只能在16以上版本下使用。
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			//MemTotal:         516452 kB  
			String line = br.readLine();
			//字符串  一组字符--串
			StringBuffer sb  = new StringBuffer();
			for(char c : line.toCharArray()){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			return Integer.parseInt(sb.toString())*1024l;  //byte
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
