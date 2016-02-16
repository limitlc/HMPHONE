package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_appmanager;
	private LinearLayout ll_loading;
	private List<AppInfo> appInfos;
	private TextView tv_status;

	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private LinearLayout ll_uninstall;
	
	
	/**
	 * ���������Ŀ
	 */
	private AppInfo appInfo;

	/**
	 * �û����򼯺�
	 */
	private List<AppInfo> userAppInfos;

	/**
	 * ϵͳ���򼯺�
	 */
	private List<AppInfo> systemAppInfos;

	private PopupWindow popupWindow;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.GONE);
			lv_appmanager.setAdapter(new AppInfoAdapter());
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		tv_avail_sd.setText("SD�����ã�"
				+ getTotalSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		tv_avail_rom.setText("�ڴ���ã�"
				+ getTotalSpace(Environment.getDataDirectory()
						.getAbsolutePath()));
		lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
		tv_status = (TextView) findViewById(R.id.tv_status);

		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_appmanager.getItemAtPosition(position);
				if (obj != null) {
					dismissPopupWindow();
					appInfo = (AppInfo) obj;
					View contentView = View.inflate(getApplicationContext(),
							R.layout.popup_item, null);
					ll_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_uninstall);
					ll_start = (LinearLayout) contentView
							.findViewById(R.id.ll_start);
					ll_share = (LinearLayout) contentView
							.findViewById(R.id.ll_share);
					ll_share.setOnClickListener(AppManagerActivity.this);
					ll_start.setOnClickListener(AppManagerActivity.this);
					ll_uninstall.setOnClickListener(AppManagerActivity.this);

					// ���Ŷ�����һ��ǰ�� ���Ǵ�������б���
					popupWindow = new PopupWindow(contentView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					// �� ע�⣺ ����Ҫ���ñ���
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					//�����ֻ��ֻ��ķֱ��� ��60dip ת���� ��ͬ��ֵ px
					int px = DensityUtil.dip2px(getApplicationContext(), 60);
					System.out.println(px);
					popupWindow.showAtLocation(parent, Gravity.TOP
							+ Gravity.LEFT, px, location[1]);
					AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
					aa.setDuration(200);
					ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
							1.0f, Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(200);
					AnimationSet set = new AnimationSet(false);
					set.addAnimation(sa);
					set.addAnimation(aa);
					contentView.startAnimation(set);
				}

			}
		});
		// tv_status.setPadding(0, 0, 0, 0);
		lv_appmanager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����(" + systemAppInfos.size() + ")");
					} else {
						tv_status.setText("�û�����(" + userAppInfos.size() + ")");
					}
				}
			}
		});

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		fillData();

	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appinfo : appInfos) {
					if (appinfo.isUserApp()) {
						userAppInfos.add(appinfo);
					} else {
						systemAppInfos.add(appinfo);
					}
				}

				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * ��ȡĳ��·�����õĿռ�
	 * 
	 * @param path
	 * @return
	 */
	public String getTotalSpace(String path) {
		StatFs stat = new StatFs(path);
		long count = stat.getAvailableBlocks();
		long size = stat.getBlockSize();
		return Formatter.formatFileSize(this, count * size);
	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppInfoAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// ��������textview��item ���� +1 +1
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		// ����ÿ��λ����ʾ�����ݡ�
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;

			if (position == 0) {// ��ʾһ��textview�����û��ж��ٸ��û�Ӧ��
				TextView tv = new TextView(getApplicationContext());
				tv.setText("�û�����  (" + userAppInfos.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("ϵͳ����  (" + systemAppInfos.size() + ")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_app_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_location);
				view.setTag(holder);
			}

			holder.iv.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�");
			} else {
				holder.tv_location.setText("�ⲿ�洢��");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			AppInfo appInfo;
			if (position == 0) {// ��ʾһ��textview�����û��ж��ٸ��û�Ӧ��
				return null;
			} else if (position == (userAppInfos.size() + 1)) {
				return null;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_location;
	}

	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_share:
			Log.i(TAG, "����" + appInfo.getName());
			shareApplication();
			break;
		case R.id.ll_start:
			Log.i(TAG, "������" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_uninstall:
			Log.i(TAG, "ж�أ�" + appInfo.getName());
			uninstall();
			break;
		}
	}
	
	
	/**
	 * ����Ӧ��
	 */
	private void startApplication() {
		Intent intent = new Intent();
		String packname = appInfo.getPackname();
		PackageManager pm = getPackageManager();
		try {
			PackageInfo  packinfo = pm.getPackageInfo(packname, PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = packinfo.activities;
			if(activityInfos!=null&&activityInfos.length>0){
				ActivityInfo activityinfo = activityInfos[0];
				intent.setClassName(packname, activityinfo.name);
				startActivity(intent);
			}else{
				Toast.makeText(this, "��ѽ�����Ӧ�ó���û����", 0).show();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, "û�������Ӧ�á�", 0).show();
		}
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		// <action android:name="android.intent.action.SEND" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:mimeType="text/plain" />
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"�Ƽ���ʹ��һ���������������ƽУ�"
						+ appInfo.getName()
						+ "�����ص�ַ��https://play.google.com/store/apps/details?id="+appInfo.getPackname());
		startActivity(intent);
	}

	private void uninstall() {
		if (appInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + appInfo.getPackname()));
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(this, "ϵͳӦ����Ҫ��rootȨ�޺����ж��", 0).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
}
