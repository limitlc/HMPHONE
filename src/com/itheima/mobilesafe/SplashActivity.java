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
			case ENTER_HOME:// ����������
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:// ��ʾ���¶Ի���
				Log.i(TAG, "��ʾ���¶Ի���");
				showUpdateDialog();
				break;
			case URL_ERROR:// url·������
				Toast.makeText(SplashActivity.this, "������·������", 0).show();
				enterHome();
				break;
			case NETWORK_ERROR:// �������Ӵ���
				Toast.makeText(getApplicationContext(), "�������Ӵ���", 0).show();
				enterHome();
				break;
			case JSON_ERROR:// ����json���ݴ���
				Toast.makeText(getApplicationContext(), "����json���ݴ���", 0)
						.show();
				enterHome();
				break;
			}

		};
	};

	private TextView tv_splash_version;
	private TextView tv_splash_updateinfo;
	/**
	 * �°汾���ص�·��
	 */
	private String path;
	/**
	 * �°汾��������Ϣ
	 */
	private String description;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		// ����һ��Ӧ�ó���Ŀ��ͼ��
		installShortCut();
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_updateinfo = (TextView) findViewById(R.id.tv_splash_updateinfo);
		tv_splash_version.setText("�汾��:" + getVersion());
		
		boolean update = sp.getBoolean("update", false);
		// ��ʼ�����ݿ��ļ�
		// ��asset�µ����ݿ� ������ϵͳ��Ŀ¼����
		copyDB("address.db");
		copyDB("commonnum.db");
		copyDB("antivirus.db");
		if (update) {
			// ��ȡ�������˵�������Ϣ�� ��ȡ���µİ汾�š�
			checkVersion();
		} else {// �Զ�����û�п�����
				// �ȴ�2���� ���������档
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
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "������ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.safe));
		//�ֻ���ʿ��������ͼ��
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
	 * �����ʲ�Ŀ¼�µ����ݿ��ļ�
	 */
	private void copyDB(String dbfilename) {
		File file = new File(getFilesDir(), dbfilename);
		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "���ݿ��ļ��Ѿ��������ˣ������ظ�����");
		} else {
			try {
				// ���ݿ��ļ�ֻ��Ҫ����һ�Σ�����Ѿ������ɹ��ˡ��Ժ�Ͳ���Ҫ�ظ��Ŀ�����
				AssetManager am = getAssets();
				InputStream is = am.open(dbfilename);
				// ����һ���ļ� /data/data/����/files/address.db
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
	 * ��ʾ�������ѶԻ���
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
		builder.setTitle("��������");
		builder.setMessage(description);
		builder.setPositiveButton("���̸���", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// �����µ�apk ·�� path
					FinalHttp fh = new FinalHttp();
					fh.download(path, Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/update.apk",
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									Toast.makeText(getApplicationContext(),
											"����ʧ��", 0).show();
									enterHome();
									t.printStackTrace();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									int progress = (int) (current * 100 / count);
									tv_splash_updateinfo.setText("�������أ�"
											+ progress + "%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installApk(t);
								}

								/**
								 * ��װһ��apk�ļ�
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
					Toast.makeText(getApplicationContext(), "sd�������ã�����ʧ��", 0)
							.show();
					enterHome();
					return;
				}
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {
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
		this.finish();// �رյ���ǰ��splash���档
	}

	/**
	 * ���汾�ķ���
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
						// �õ����Ƿ������˵İ汾��Ϣ��
						String version = (String) obj.get("version");
						description = (String) obj.get("description");
						path = (String) obj.get("path");
						if (getVersion().equals(version)) {
							// �汾��һ�£�ֱ�ӽ������������
							msg.what = ENTER_HOME;
						} else {
							// �汾�Ų�һ�£������������ѶԻ���
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
	 * ��ȡ��ǰӦ�ó���İ汾��
	 * 
	 * @return
	 */
	public String getVersion() {
		PackageManager pm = getPackageManager();
		try {
			// ����ľ����嵥�ļ�����Ϣ��
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// �϶����ᷢ����
			// can't reach
			return "";
		}
	}
}
