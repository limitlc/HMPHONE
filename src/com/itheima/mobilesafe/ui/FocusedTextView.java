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
	 * 其实当前控件并没有真正的得到焦点，只是欺骗了系统。
	 * 让系统认为我当前控件是有焦点的。
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
