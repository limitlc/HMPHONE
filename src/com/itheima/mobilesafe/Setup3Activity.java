package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_setup3_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		et_setup3_phone.setText(sp.getString("safenumber", ""));
	}
	@Override
	public void showpre() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		//关闭当前的界面。
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	@Override
	public void shownext() {
		String safenumber = et_setup3_phone.getText().toString().trim();
		if(TextUtils.isEmpty(safenumber)){
			Toast.makeText(this, "请先设置安全号码", 0).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safenumber", safenumber);
		editor.commit();
		Intent intent = new Intent(this,Setup4Activity.class);
		startActivity(intent);
		//关闭当前的界面。
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	public void selectContact(View view){
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String phone = data.getStringExtra("phone").replace("-", "");
		et_setup3_phone.setText(phone);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
