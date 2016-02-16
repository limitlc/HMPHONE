package com.itheima.mobilesafe;

import com.itheima.mobilesafe.db.dao.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.renderscript.Program.TextureType;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	private static final String TAG = "NumberAddressQueryActivity";
	private EditText et_phone;
	private TextView tv_result;
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//得到手机振动器的服务
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		setContentView(R.layout.activity_number_query);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.addTextChangedListener(new TextWatcher() {
			//当文本发生变化的时候调用的方法
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length()>=3){
						String address = AddressDao.getAddress(s.toString());
						tv_result.setText(address);
					}
			}
			//当文本变化之前调用的方法
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//文本变化后调用的方法
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		tv_result = (TextView) findViewById(R.id.tv_result);
	}
	
	public void query(View view){
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
			vibrator.vibrate(new long[]{100,200,100,300,50,200}, 1);
			return;
		}else{
			Log.i(TAG,"查询："+phone+"的归属地");
			String address = AddressDao.getAddress(phone);
			tv_result.setText(address);
		}
	}
}
