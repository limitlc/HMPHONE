package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.GPSService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"我是清单文件配置的广播接受者");
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			String sender = smsMessage.getOriginatingAddress();
			if("#*location*#".equals(body)){
				Log.i(TAG,"返回手机的位置信息");
				Intent i = new Intent(context,GPSService.class);
				context.startService(i);
				SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				String lastlocation = sp.getString("lastlocation", null);
				if(TextUtils.isEmpty(lastlocation)){
					SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
				}else{
					SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
				}
				abortBroadcast();
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"播放报警音乐");
				MediaPlayer mediaplayer = MediaPlayer.create(context, R.raw.ylzs);
				mediaplayer.setVolume(1.0f, 1.0f);
				//mediaplayer.setLooping(true);
				mediaplayer.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){
				Log.i(TAG,"清除手机数据");
				//dpm.wipedata();
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"远程锁屏");
				abortBroadcast();
				//dpm.locknow();
			}
		}
		
	}

}
