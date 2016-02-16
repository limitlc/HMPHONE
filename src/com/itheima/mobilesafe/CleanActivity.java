package com.itheima.mobilesafe;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class CleanActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean);
		
		TabHost tabHost = getTabHost();//��ȡ��ǰ����tabhost������������������Է��úܶ��С��ǩ
		
		TabSpec tab1 = tabHost.newTabSpec("��������");
		TabSpec tab2 = tabHost.newTabSpec("SD������");
		
		//��������ѡ��ı�ǩ
		tab1.setIndicator("��������");
		tab2.setIndicator("SD������");
		//��������ѡ����������
		tab1.setContent(new Intent(this,CleanCacheActivity.class));
		tab2.setContent(new Intent(this,CleanSDActivity.class));
		
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		
		
	}
}
