package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.����һ������ʶ����
	protected GestureDetector mGestureDetector;
	
	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.��ʼ��һ������ʶ����
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// ����ָ����Ļ�ϻ�����ʱ����õķ���
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
							Toast.makeText(getApplicationContext(), "����������", 0)
									.show();
							return true;
						}
						if (Math.abs(velocityX) < 200) {
							Toast.makeText(getApplicationContext(), "��㻬��", 0)
									.show();
							return true;
						}

						if ((e2.getRawX() - e1.getRawX()) > 200) {
							// ��ʾ��һ�����档 ��ָ�������һ���
							showpre();
							return true;
						}

						if ((e1.getRawX() - e2.getRawX()) > 200) {
							// ��ʾ��һ������ ��ָ�������󻬶�
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

	// 3�����������ʶ����,�����Ļ�ϵ������¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
