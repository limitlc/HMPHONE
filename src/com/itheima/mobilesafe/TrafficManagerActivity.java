package com.itheima.mobilesafe;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TrafficStats.getMobileTxBytes(); //3g/2g�����ϴ���������
		// TrafficStats.getMobileRxBytes();//3g/2g�������ص�������
		// TrafficStats.getTotalRxBytes();// �ֻ��� + wifi
		// TrafficStats.getTotalTxBytes();
		// TrafficStats.getUidRxBytes(uid);
		// TrafficStats.getUidTxBytes(uid);
		setContentView(R.layout.activity_traffic_manager);
	}
}
