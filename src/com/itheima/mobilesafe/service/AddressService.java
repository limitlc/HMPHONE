package com.itheima.mobilesafe.service;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	private TelephonyManager tm;
	private MyPhoneListener listener;
	private OutCallReceiver receiver;
	private WindowManager wm;

	/**
	 * 归属地显示的view对象
	 */
	private View view;

	private SharedPreferences sp;

	// 在服务代码的内部 创建一个内部类
	// 利用代码的方式 注册一个广播接受者
	// 让广播接收者的存活周期 跟服务保持一致了。
	private class OutCallReceiver extends BroadcastReceiver {
		private static final String TAG = "OutCallReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "我是服务内部的广播接受者，有电话打出去了。。。");
			String phone = getResultData();
			String result = AddressDao.getAddress(phone);
			// Toast.makeText(context, result, 1).show();
			showMyToast(result);
		}
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 采用代码的方式 注册广播接受者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		super.onCreate();
	}

	private class MyPhoneListener extends PhoneStateListener {
		// 当呼叫的状态发生变化的时候 调用的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(getApplicationContext(), address, 1).show();
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 空闲状体
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		// 服务停止的时候 取消注册广播接受者
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
	WindowManager.LayoutParams params;
	/**
	 * 显示一个自定义的土司
	 * 
	 * @param address
	 */
	public void showMyToast(String address) {
		int which = sp.getInt("which", 0);
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] bgs = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// 直接利用窗体管理器 添加一个view对象到整个手机系统的窗体上
		view = View.inflate(this, R.layout.toast_address, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x +=dx;
					params.y +=dy;
					if(params.x<0){
						params.x = 0;
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.x > wm.getDefaultDisplay().getWidth()-view.getWidth()){
						params.x  = wm.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if(params.y > wm.getDefaultDisplay().getHeight()-view.getHeight()){
						params.y  = wm.getDefaultDisplay().getHeight()-view.getHeight();
					}
					wm.updateViewLayout(view, params);
					//重新初始化手指的位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					break;
				}
				return true;
			}
		});
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_location);
		tv.setText(address);
		params = new WindowManager.LayoutParams();
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// 窗体的类型。
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
		/*new Thread(){
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wm.removeView(view);
			};
		}.start();*/
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
