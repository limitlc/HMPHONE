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
		// 检查当前手机的sim卡， 读取原来绑定的sim卡 ，如果发现两个sim卡不一致 说明手机可能被盗了。需要偷偷的发送报警信息。
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			String bindsim = sp.getString("sim", "") + "a";
			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String realsim = tm.getSimSerialNumber();
			if (bindsim.equals(realsim)) {
				// 就是你的手机和你的卡
			} else {
				System.out.println("sim卡串号发生了变化，发送报警的信息");
				String safenumber = sp.getString("safenumber", "");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(safenumber, null, "sim card changed!", null, null);
			}
		}
	}

}
