package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itheima.mobilesafe.domain.AppInfo;

public class AppInfoProvider {

	/**
	 * ��ȡ�ֻ��������еİ�װ��Ӧ�ó�����Ϣ
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		//�õ���������
		PackageManager pm = context.getPackageManager();
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		List<PackageInfo> packinfos = pm.getInstalledPackages(0);
		for(PackageInfo packinfo : packinfos){
			String packname = packinfo.packageName;
			AppInfo appInfo = new AppInfo();
			Drawable icon = packinfo.applicationInfo.loadIcon(pm);
			String name = packinfo.applicationInfo.loadLabel(pm).toString()+packinfo.applicationInfo.uid;
			//Ӧ�ó����������־�� �����������־�����
			int flags = packinfo.applicationInfo.flags;//Ӧ�ý��Ĵ��⿨
			if((flags & ApplicationInfo.FLAG_SYSTEM)  == 0){
				//�û�Ӧ��
				appInfo.setUserApp(true);
			}else{
				//ϵͳӦ��
				appInfo.setUserApp(false);
			}
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)  == 0){
				//�ֻ��ڴ�
				appInfo.setInRom(true);
			}else{
				//�ⲿ�洢
				appInfo.setInRom(false);
			}
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfo.setPackname(packname);
			appinfos.add(appInfo);
		}
		return appinfos;
	}
}
