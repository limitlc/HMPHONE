package com.itheima.mobilesafe;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.service.AutoKillService;
import com.itheima.mobilesafe.utils.ServiceStatusUtils;

public class TaskManagerSettingActivity extends Activity {
	
	private CheckBox cb_show_system;
	private CheckBox cb_auto_kill;
	private SharedPreferences sp;
	private Timer timer;
	private TimerTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("哈哈，我干了一次活");
				
			}
		};
		timer.schedule(task, 1000, 5000);
		
		setContentView(R.layout.activity_taskmanager_setting);
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean showsystem = sp.getBoolean("showsystem", true);
		if(showsystem){
			cb_show_system.setText("当前状态：显示系统进程");
			cb_show_system.setChecked(true);
		}else{
			cb_show_system.setText("当前状态：不显示系统进程");
			cb_show_system.setChecked(false);
		}
		
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_show_system.setText("当前状态：显示系统进程");
				}else{
					cb_show_system.setText("当前状态：不显示系统进程");
				}
				Editor editor = sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
			}
		});
		
		
		cb_auto_kill = (CheckBox) findViewById(R.id.cb_auto_kill);
		final Intent intent = new Intent(this,AutoKillService.class);
		boolean result = ServiceStatusUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.AutoKillService");
		if(result){
			cb_auto_kill.setText("当前状态：锁屏清理已经开启");
		}else{
			cb_auto_kill.setText("当前状态：锁屏清理没有开启");
		}
		cb_auto_kill.setChecked(result);
		cb_auto_kill.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_auto_kill.setText("当前状态：锁屏清理已经开启");
					startService(intent);
				}else{
					cb_auto_kill.setText("当前状态：锁屏清理没有开启");
					stopService(intent);
				}
			}
		});
	}
	@Override
	protected void onDestroy() {
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
		super.onDestroy();
	}
}
