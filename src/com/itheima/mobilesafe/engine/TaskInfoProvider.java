package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {
	
	/**
	 * ��ȡ�������еĽ�����Ϣ
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for(RunningAppProcessInfo processInfo : processInfos){
			TaskInfo taskInfo = new TaskInfo();
			String packname = processInfo.processName;
			taskInfo.setPackname(packname);
			 MemoryInfo[] memoryInfos =	am.getProcessMemoryInfo(new int[]{processInfo.pid});
			long memsize  = memoryInfos[0].getTotalPrivateDirty()*1024l;//�õ�ĳ�������ܵ��ڴ��С
			taskInfo.setMemsize(memsize);
			try {
				PackageInfo packInfo = pm.getPackageInfo(packname, 0);
				Drawable icon = packInfo.applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = packInfo.applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){
					//�û�����
					taskInfo.setUserTask(true);
				}else{
					//ϵͳ����
					taskInfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				//ϵͳ�ں˽��� û������
				taskInfo.setName(packname);
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.default_icon));
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
