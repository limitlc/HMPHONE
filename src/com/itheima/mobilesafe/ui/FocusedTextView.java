package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusedTextView(Context context) {
		super(context);
	}

	/**
	 * ��ʵ��ǰ�ؼ���û�������ĵõ����㣬ֻ����ƭ��ϵͳ��
	 * ��ϵͳ��Ϊ�ҵ�ǰ�ؼ����н���ġ�
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
