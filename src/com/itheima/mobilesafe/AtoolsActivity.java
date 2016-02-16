package com.itheima.mobilesafe;

import java.io.File;

import com.itheima.mobilesafe.utils.SmsTools;
import com.itheima.mobilesafe.utils.SmsTools.BackUpCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * ��������ز�ѯ
	 * 
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ú����ѯ
	 * 
	 * @param view
	 */
	public void commonNumberQuery(View view) {
		Intent intent = new Intent(this, CommonNumberQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ŵı���
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			final File file = new File(
					Environment.getExternalStorageDirectory(), "smsbackup.xml");
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("�԰����꣬���ڱ�����...");
			pd.show();
			new Thread() {
				public void run() {
					try {
						SmsTools.backup(getApplicationContext(), file.getAbsolutePath(), new BackUpCallBack() {
							@Override
							public void onSmsBackup(int progress) {
								pd.setProgress(progress);
							}
							
							@Override
							public void beforeSmsBackup(int total) {
								pd.setMax(total);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					pd.dismiss();
				};
			}.start();
		} else {
			Toast.makeText(this, "sd������", 0).show();
		}
	}
	
	/**
	 * �������������
	 */
	public void enterApplock(View view){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}