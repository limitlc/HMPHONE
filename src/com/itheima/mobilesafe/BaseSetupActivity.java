package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.声明一个手势识别器
	protected GestureDetector mGestureDetector;
	
	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.初始化一个手势识别器
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// 当手指在屏幕上滑动的时候调用的方法
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
							Toast.makeText(getApplicationContext(), "不能这样划", 0)
									.show();
							return true;
						}
						if (Math.abs(velocityX) < 200) {
							Toast.makeText(getApplicationContext(), "快点滑动", 0)
									.show();
							return true;
						}

						if ((e2.getRawX() - e1.getRawX()) > 200) {
							// 显示上一个界面。 手指从左向右滑动
							showpre();
							return true;
						}

						if ((e1.getRawX() - e2.getRawX()) > 200) {
							// 显示下一个界面 手指从右向左滑动
							shownext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

	}

	public abstract void showpre();

	public abstract void shownext();

	public void next(View view) {
		shownext();
	}

	public void pre(View view) {
		showpre();
	}

	// 3。用这个手势识别器,检查屏幕上的手势事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
