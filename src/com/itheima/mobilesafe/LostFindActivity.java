package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		super.onCreate(savedInstanceState);
		//判断用户是否进行过设置向导，如果没有进行过 ，需要定向界面到设置向导
		if(configed){
			//进行过设置向导
			setContentView(R.layout.activity_lost_find);
			iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
			tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
			tv_lostfind_number.setText(sp.getString("safenumber", ""));
		}else{
			//定向界面到 设置向导界面。
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			finish();//退出当前界面。
		}
	}
	
	public void reEntrySetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();//退出当前界面。
	}
	
}
