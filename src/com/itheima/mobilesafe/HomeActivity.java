package com.itheima.mobilesafe;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import com.itheima.mobilesafe.utils.Md5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private SharedPreferences sp;
	private static final String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
	private static final int[] icons = { R.drawable.safe,
			R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
			R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings };
	protected static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_home);
		
		// 初始化接口，应用启动的时候调用
		// 参数：appId, appSecret, 调试模式
//		AdManager.getInstance(this).init("8e760a752e0c3139",
//				"0da6d5398f1f5ccc", true);
//        LinearLayout adLayout = (LinearLayout) findViewById(R.id.ad_layout);
//        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
//        adLayout.addView(adView);
		
		gv_home = (GridView) findViewById(R.id.gv_home);

		gv_home.setAdapter(new HomeAdapter());
		
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent ;
				switch (position) {
				case 0: //手机防盗
					//弹出对话框
					showLostFindDialog();
					break;
				case 1:
					intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://进程管理器
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4://流量统计
					intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5://手机杀毒
					intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6://缓存清理
					intent = new Intent(HomeActivity.this,CleanActivity.class);
					startActivity(intent);
					break;
				case 7:
					intent = new Intent(HomeActivity.this,AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8://设置中心
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	/**
	 * 显示进入手机防盗的对话框
	 */
	protected void showLostFindDialog() {
		//如果用户没设置过密码 设置密码， 如果设置过 输入密码
		if(isSetupPwd()){
			showEnterDialog();
		}else{
			showSetupPwdDialog();
		}
		
	}
	private EditText et_pwd;
	private EditText et_pwd_confirm;
	private Button bt_ok;
	private Button bt_cancel;
	private AlertDialog dialog;
	/**
	 *显示设置密码的对话框
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_setup_password, null);
		et_pwd= (EditText) view.findViewById(R.id.et_pwd);
		et_pwd_confirm = (EditText) view.findViewById(R.id.et_pwd_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString().trim();
				String pwd_confirm = et_pwd_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
					Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
					return;
				}
				if(pwd.equals(pwd_confirm)){
					//存储这个密码
					Editor editor = sp.edit();
					editor.putString("password", Md5Utils.encode(pwd));
					editor.commit();
					dialog.dismiss();
					Log.i(TAG,"密码已经设置完毕了。进入手机防盗界面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				}else{
					Toast.makeText(getApplicationContext(), "两次密码不一致", 0).show();
					return;
				}
				
			}
		});
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * 输入密码的对话框
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_password, null);
		et_pwd= (EditText) view.findViewById(R.id.et_pwd);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString().trim();
				String saved_pwd = sp.getString("password", "");//加密后的md5
				if(TextUtils.isEmpty(pwd)){
					Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
					return;
				}
				if(Md5Utils.encode(pwd).equals(saved_pwd)){
					//密码正确 进入手机防盗界面
					Log.i(TAG,"密码正确 进入手机防盗界面");
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "密码错误", 0).show();
					return;
				}
			}
		});
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	/**
	 * 判断用户是否设置过密码
	 * @return
	 */
	private boolean isSetupPwd(){
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}
	

	private class HomeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return names.length;
		}

		// 返回每个位置对应的view对象。
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.list_home_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_home_item);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_home_item);
			tv_name.setText(names[position]);
			iv_icon.setImageResource(icons[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

}
