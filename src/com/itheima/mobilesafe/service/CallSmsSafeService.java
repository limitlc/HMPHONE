package com.itheima.mobilesafe.service;

import java.lang.reflect.Method;
import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	
	//������ǰ���е�״̬
	private TelephonyManager tm;
	private MyPhoneListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"���Ƿ����ڲ��Ĺ㲥�����ߣ��ҽ��ܵ��� ���ŵ����Ĺ㲥�¼�");
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if("2".equals(mode)||"3".equals(mode)){
					Log.i(TAG,"���ص�����������");
					abortBroadcast();
					//TODO:��������ص��Ķ��ż�¼������
				}
				
				String body = smsMessage.getMessageBody();
				//������ѯ���ݿ�
				if(body.contains("fapiao")){
					Log.i(TAG,"���ص�����Ʊ�Ķ���"); 
					abortBroadcast();
					//TODO:��������ص��Ķ��ż�¼������
				}
			}
		}
	}
	
	
	@Override
	public void onCreate() {
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//�����绰��״̬
		
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver, filter);
		super.onCreate();
	}
	
	private class MyPhoneListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://����״̬
				String mode = dao.findMode(incomingNumber);
				if("1".equals(mode)||"3".equals(mode)){
					Log.i(TAG,"���Ǻ��������룬�Ҷϵ绰������");
					//���Ӻ��м�¼�����ɣ�������м�¼�����ˡ�ɾ�����м�¼��
					Uri url = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(url, true, new CallLogObserver(new Handler(), incomingNumber));
					endcall();// ���̰ѵ绰�Ҷ��ˡ�  ���Ǻ��м�¼������ ������һ��ͬ���Ĵ��롣 ��һ���첽����
				}
				break;
			}
		}
	}
	
	private class CallLogObserver extends ContentObserver{
		private String incomingNumber;
		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		//���ݱ仯�����onchage����
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getContentResolver().unregisterContentObserver(this);
			deleteCalllog(incomingNumber);
		}
	}
	
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}

	/**
	 * ������м�¼
	 * @param incomingNumber
	 */
	public void deleteCalllog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri url = Uri.parse("content://call_log/calls");
		resolver.delete(url, "number=?", new String[]{incomingNumber});
	}

	/**
	 * �Ҷϵ绰
	 */
	public void endcall() {
		//ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			Class clazz = 	CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder b  = (IBinder) method.invoke(null, TELEPHONY_SERVICE); 
			//��ȡ����ԭ��δ����װ��ϵͳ�绰�Ĺ������
			ITelephony iTelephony = ITelephony.Stub.asInterface(b);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
