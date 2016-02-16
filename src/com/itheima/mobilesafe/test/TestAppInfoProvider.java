package com.itheima.mobilesafe.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;

public class TestAppInfoProvider extends AndroidTestCase {
	
	public void testGetAllApp() throws Exception{
		List<AppInfo> infos = AppInfoProvider.getAppInfos(getContext());
		for(AppInfo info:infos){
			System.out.println(info.toString());
		}
		
	}
}
