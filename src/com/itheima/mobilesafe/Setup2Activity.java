package com.itheima.mobilesafe;

import com.itheima.mobilesafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_setup2_bindsim;
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		setContentView(R.layout.activity_setup2);
		siv_setup2_bindsim = (SettingItemView) findViewById(R.id.siv_setup2_bindsim);
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			siv_setup2_bindsim.setChecked(false);
		}else{
			siv_setup2_bindsim.setChecked(true);
		}
		siv_setup2_bindsim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (siv_setup2_bindsim.isChecked()) {
					Editor editor = sp.edit();
					editor.putString("sim", null);
					editor.commit();
					siv_setup2_bindsim.setChecked(false);
				} else {
					// 绑定sim卡就把sim卡的串号给存起来。
					String sim = tm.getSimSerialNumber();
					Editor editor = sp.edit();
					editor.putString("sim", sim);
					editor.commit();
					siv_setup2_bindsim.setChecked(true);
				}
			}
		});
	}

	@Override
	public void showpre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		// 关闭当前的界面。
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	@Override
	public void shownext() {
		//判断 是否绑定了sim卡
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "请先绑定sim卡", 0).show();
			return;
		}
		
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		// 关闭当前的界面。
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
}
