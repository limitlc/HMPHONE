package com.itheima.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources.Theme;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.service.UpdateWidgetService;
import com.itheima.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";

	protected static final int ENTER_HOME = 1;

	protected static final int SHOW_UPDATE_DIALOG = 2;

	protected static final int URL_ERROR = 3;

	protected static final int NETWORK_ERROR = 4;

	protected static final int JSON_ERROR = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// 进入主界面
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:// 提示更新对话框
				Log.i(TAG, "提示更新对话框");
				showUpdateDialog();
				break;
			case URL_ERROR:// url路径错误
				Toast.makeText(SplashActivity.this, "服务器路径错误。", 0).show();
				enterHome();
				break;
			case NETWORK_ERROR:// 网络连接错误
				Toast.makeText(getApplicationContext(), "网络连接错误。", 0).show();
				enterHome();
				break;
			case JSON_ERROR:// 解析json数据错误
				Toast.makeText(getApplicationContext(), "解析json数据错误。", 0)
						.show();
				enterHome();
				break;
			}

		};
	};

	private TextView tv_splash_version;
	private TextView tv_splash_updateinfo;
	/**
	 * 新版本下载的路径
	 */
	private String path;
	/**
	 * 新版本的描述信息
	 */
	private String description;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 创建一个应用程序的快捷图标
		installShortCut();
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_updateinfo = (TextView) findViewById(R.id.tv_splash_updateinfo);
		tv_splash_version.setText("版本号:" + getVersion());
		
		boolean update = sp.getBoolean("update", false);
		// 初始化数据库文件
		// 把asset下的数据库 拷贝到系统的目录里面
		copyDB("address.db");
		copyDB("commonnum.db");
		copyDB("antivirus.db");
		if (update) {
			// 读取服务器端的配置信息， 获取最新的版本号。
			checkVersion();
		} else {// 自动更新没有开启。
				// 等待2秒钟 进入主界面。
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash_root).startAnimation(aa);
	}

	private void installShortCut() {
		boolean installedshortcut = sp.getBoolean("installedshortcut", false);
		if(installedshortcut){
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.safe));
		//手机卫士开启的意图。
		Intent homeIntent = new Intent();
		homeIntent.setAction("com.itheima.mobilesafe.home");
		homeIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, homeIntent);
		sendBroadcast(intent);
		Editor editor = sp.edit();
		editor.putBoolean("installedshortcut", true);
		editor.commit();
	}

	/**
	 * 拷贝资产目录下的数据库文件
	 */
	private void copyDB(String dbfilename) {
		File file = new File(getFilesDir(), dbfilename);
		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "数据库文件已经拷贝过了，无需重复拷贝");
		} else {
			try {
				// 数据库文件只需要拷贝一次，如果已经拷贝成功了。以后就不需要重复的拷贝了
				AssetManager am = getAssets();
				InputStream is = am.open(dbfilename);
				// 创建一个文件 /data/data/包名/files/address.db
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 显示更新提醒对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		// builder.setCancelable(false);
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		builder.setTitle("更新提醒");
		builder.setMessage(description);
		builder.setPositiveButton("立刻更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 下载新的apk 路径 path
					FinalHttp fh = new FinalHttp();
					fh.download(path, Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/update.apk",
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									Toast.makeText(getApplicationContext(),
											"更新失败", 0).show();
									enterHome();
									t.printStackTrace();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									int progress = (int) (current * 100 / count);
									tv_splash_updateinfo.setText("正在下载："
											+ progress + "%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installApk(t);
								}

								/**
								 * 安装一个apk文件
								 * 
								 * @param t
								 */
								private void installApk(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}
							});
				} else {
					Toast.makeText(getApplicationContext(), "sd卡不可用，更新失败", 0)
							.show();
					enterHome();
					return;
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();

			}
		});

		builder.show();
	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		this.finish();// 关闭掉当前的splash界面。
	}

	/**
	 * 检查版本的方法
	 */
	private void checkVersion() {
		new Thread() {
			public void run() {
				long startTime = System.currentTimeMillis();
				Message msg = Message.obtain();
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, result);
						JSONObject obj = new JSONObject(result);
						// 得到的是服务器端的版本信息。
						String version = (String) obj.get("version");
						description = (String) obj.get("description");
						path = (String) obj.get("path");
						if (getVersion().equals(version)) {
							// 版本号一致，直接进入程序主界面
							msg.what = ENTER_HOME;
						} else {
							// 版本号不一致，弹出更新提醒对话框。
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * 获取当前应用程序的版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		PackageManager pm = getPackageManager();
		try {
			// 代表的就是清单文件的信息。
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 肯定不会发生。
			// can't reach
			return "";
		}
	}
}
