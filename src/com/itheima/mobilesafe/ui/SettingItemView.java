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
public class SettingItemView extends RelativeLayout{
	private TextView tv_desc;
	private TextView tv_title;
	private CheckBox cb_status;
	
	private String desc_on;
	private String desc_off;
	private String title;
	
	/**
	 * ��ʼ�������Զ������Ͽؼ�
	 */
	private void initView(Context context) {
		//ת�������ļ�->view���� ���view����ֱ�ӹ������Լ����ϡ�
		View.inflate(context, R.layout.ui_setting_item_view, this);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}


	/**
	 * �ڲ����ļ����涨������е�����  ���ᱻ��װ�� AttributeSet�����Լ�������
	 * @param context
	 * @param attrs
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "desc_off");
		setTitle(title);
		setDesc(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	/**
	 * �жϵ�ǰ��Ͽؼ��Ƿ�ѡ�С�
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	/**
	 * ������Ͽؼ��Ĺ�ѡ״̬
	 * @param checked
	 */
	public void setChecked(boolean checked){
		if(checked){
			setDesc(desc_on);
		}else{
			setDesc(desc_off);
		}
		cb_status.setChecked(checked);
	}
	
	/**
	 * �����Զ�����Ͽؼ����ı�
	 */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	
	/**
	 * �����Զ�����Ͽؼ��ı���
	 * @param text
	 */
	public void setTitle(String text){
		tv_title.setText(text);
	}
	

}
