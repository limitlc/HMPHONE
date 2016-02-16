package com.itheima.mobilesafe.ui;

import com.itheima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 做一个特殊的相对布局。 一出生 布局里面显示的是2个textview 一个checkbox 还有一个view
 * @author Administrator
 *
 */
public class SettingClickView extends RelativeLayout{
	private TextView tv_desc;
	private TextView tv_title;
	
	/**
	 * 初始化我们自定义的组合控件
	 */
	private void initView(Context context) {
		//转化布局文件->view对象 这个view对象直接挂载在自己身上。
		View.inflate(context, R.layout.ui_setting_click_view, this);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}


	/**
	 * 在布局文件里面定义的所有的属性  都会被封装到 AttributeSet的属性集合里面
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}
	
	/**
	 * 设置自定义组合控件的文本
	 */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	/**
	 * 设置自定义组合控件的文本
	 */
	public void setTitle(String text){
		tv_title.setText(text);
	}
	

}
