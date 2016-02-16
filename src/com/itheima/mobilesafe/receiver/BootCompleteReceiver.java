package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ��鵱ǰ�ֻ���sim���� ��ȡԭ���󶨵�sim�� �������������sim����һ�� ˵���ֻ����ܱ����ˡ���Ҫ͵͵�ķ��ͱ�����Ϣ��
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			String bindsim = sp.getString("sim", "") + "a";
			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String realsim = tm.getSimSerialNumber();
			if (bindsim.equals(realsim)) {
				// ��������ֻ�����Ŀ�
			} else {
				System.out.println("sim�����ŷ����˱仯�����ͱ�������Ϣ");
				String safenumber = sp.getString("safenumber", "");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(safenumber, null, "sim card changed!", null, null);
			}
		}
	}

}
