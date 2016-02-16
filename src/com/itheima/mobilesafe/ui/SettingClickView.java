package com.itheima.mobilesafe.ui;

import com.itheima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * ��һ���������Բ��֡� һ���� ����������ʾ����2��textview һ��checkbox ����һ��view
 * @author Administrator
 *
 */
public class SettingClickView extends RelativeLayout{
	private TextView tv_desc;
	private TextView tv_title;
	
	/**
	 * ��ʼ�������Զ������Ͽؼ�
	 */
	private void initView(Context context) {
		//ת�������ļ�->view���� ���view����ֱ�ӹ������Լ����ϡ�
		View.inflate(context, R.layout.ui_setting_click_view, this);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}


	/**
	 * �ڲ����ļ����涨������е�����  ���ᱻ��װ�� AttributeSet�����Լ�������
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
	 * �����Զ�����Ͽؼ����ı�
	 */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	/**
	 * �����Զ�����Ͽؼ����ı�
	 */
	public void setTitle(String text){
		tv_title.setText(text);
	}
	

}
