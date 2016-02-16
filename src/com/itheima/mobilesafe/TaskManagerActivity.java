package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoProvider;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	private TextView tv_process_count;
	private TextView tv_mem_info;
	private TextView tv_status;
	// 活动管理器 activitymanager
	private ActivityManager am;

	private ListView lv_taskmanager;
	private LinearLayout ll_loading;
	private List<TaskInfo> taskInfos;
	/**
	 * 用户进程集合
	 */
	private List<TaskInfo> userTaskInfos;

	/**
	 * 系统进程集合
	 */
	private List<TaskInfo> systemTaskInfos;

	// 正在运行进程数量
	private int runningProcessCount;
	// 可用用ram内存
	private long availRam;
	// 总内存
	private long totalRam;

	private TaskManagerAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.GONE);
			adapter = new TaskManagerAdapter();
			lv_taskmanager.setAdapter(adapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_taskmanager = (ListView) findViewById(R.id.lv_taskmanager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);
		availRam = SystemInfoUtils.getAvailRam(this);
		totalRam = SystemInfoUtils.getTotalRam(this);
		tv_process_count.setText("运行中进程:" + runningProcessCount + "个");
		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
		lv_taskmanager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_taskmanager.getItemAtPosition(position);
				if (obj != null) {
					CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
					TaskInfo taskInfo = (TaskInfo) obj;
					if (taskInfo.getPackname().equals(getPackageName())) {
						return;
					}
					if (taskInfo.isChecked()) {
						cb.setChecked(false);
						taskInfo.setChecked(false);
					} else {
						cb.setChecked(true);
						taskInfo.setChecked(true);
					}
				}
			}
		});
		lv_taskmanager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem > userTaskInfos.size()) {
						tv_status.setText("系统进程(" + systemTaskInfos.size()
								+ ")");
					} else {
						tv_status.setText("用户进程(" + userTaskInfos.size() + ")");
					}
				}
			}
		});
		fillData();

	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				taskInfos = TaskInfoProvider
						.getTaskInfos(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean showsystem = sp.getBoolean("showsystem", true);
			if (showsystem) {
				return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
			} else {
				return userTaskInfos.size() + 1 ;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户进程(" + userTaskInfos.size() + ")");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统进程(" + systemTaskInfos.size() + ")");
				return tv;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_task_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_memsize = (TextView) view
						.findViewById(R.id.tv_memsize);
				holder.cb = (CheckBox) view.findViewById(R.id.cb);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_name.setText(taskInfo.getName());
			holder.tv_memsize.setText(Formatter.formatFileSize(
					getApplicationContext(), taskInfo.getMemsize()));
			holder.cb.setChecked(taskInfo.isChecked());
			if (taskInfo.getPackname().equals(getPackageName())) {
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setVisibility(View.VISIBLE);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			TaskInfo taskInfo;
			if (position == 0) {
				return null;
			} else if (position == (userTaskInfos.size() + 1)) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memsize;
		CheckBox cb;
	}

	/**
	 * 全选
	 */
	public void selectAll(View view) {
		for (TaskInfo info : userTaskInfos) {
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		for (TaskInfo info : systemTaskInfos) {
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();// 通知界面更新
	}

	/**
	 * 反选
	 * 
	 */
	public void unSelect(View view) {
		for (TaskInfo info : userTaskInfos) {
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		for (TaskInfo info : systemTaskInfos) {
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();// 通知界面更新
	}

	/**
	 * 杀死选中的进程
	 */
	public void killAll(View view) {
		int total = 0;
		long savedMem = 0;
		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : userTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				total++;
				savedMem += info.getMemsize();
				// userTaskInfos.remove(info);
				killedTaskInfos.add(info);
			}
		}
		for (TaskInfo info : systemTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				total++;
				savedMem += info.getMemsize();
				// systemTaskInfos.remove(info);
				killedTaskInfos.add(info);
			}
		}

		for (TaskInfo info : killedTaskInfos) {
			if (info.isUserTask()) {
				userTaskInfos.remove(info);
			} else {
				systemTaskInfos.remove(info);
			}
		}

		// 给用户一个土司提醒 告诉用户你干了什么事情。
		Toast.makeText(
				this,
				"杀死了" + total + "个进程,释放了"
						+ Formatter.formatFileSize(this, savedMem) + "的内存", 1)
				.show();
		runningProcessCount -= total;
		availRam += savedMem;
		tv_process_count.setText("运行中进程:" + runningProcessCount + "个");
		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
		adapter.notifyDataSetChanged();
	}

	/**
	 * 进入设置界面
	 */
	public void enterSetting(View view) {
		Intent intent = new Intent(this, TaskManagerSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged(); //---> getConut()-->  getView()
	}

}
