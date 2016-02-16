package com.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_call_sms_safe;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos;
	private LinearLayout ll_loading;
	private CallSmsSafeAdapter adapter;
	private boolean isloading = false;
	private int totalCount = 0;
	/**
	 * �������ݵĿ�ʼλ��
	 */
	private int startIndex = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter == null) {
				adapter = new CallSmsSafeAdapter();
				lv_call_sms_safe.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();// ֪ͨ���������������½��档
			}
			isloading = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		dao = new BlackNumberDao(this);
		totalCount = dao.getTotalCount();
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			// ����״̬�仯�ˡ�
			// ��ֹ-->����
			// ����-->��ֹ
			// ��ָ��������-->���Ի���
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// ��ֹ״̬
					// �жϽ����Ƿ��Ѿ�������������档
					// ��ȡ���һ���ɼ���Ŀ��λ��
					int position = lv_call_sms_safe.getLastVisiblePosition(); // 19
					int total = infos.size(); // 20
					if (isloading) {
						Toast.makeText(getApplicationContext(),
								"���ڼ������ݣ���Ҫ�ظ�ˢ�¡�", 0).show();
						return;
					}
					if (position >= (total - 5)) {

						// ָ���µĻ�ȡ���ݵĿ�ʼλ��
						startIndex += 20;
						if (startIndex >= totalCount) {
							Toast.makeText(getApplicationContext(),
									"û�и��������ˣ���������", 0).show();
							return;
						}
						Toast.makeText(getApplicationContext(),
								"�϶����������ˡ����ظ�������", 0).show();
						fillData();
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:// ���Ի���

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// ��������

					break;
				}

			}

			// ������ʱ��ִ�еķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		fillData();

	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		isloading = true;
		new Thread() {
			public void run() {
				if (infos == null) {
					infos = dao.findPart(startIndex);
				} else {// ԭ�����������Ѿ��������ˡ�
					infos.addAll(dao.findPart(startIndex));
				}
				// ����һ����Ϣ ���½��档
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {
		private static final String TAG = "CallSmsSafeAdapter";

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Ϊ�˼���view���󴴽��ĸ��� ʹ����ʷ�����view���� convertview
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				Log.i(TAG, "ʹ����ʷ�����view����" + position);
				holder = (ViewHolder) view.getTag();
			} else {
				Log.i(TAG, "�����µ�view����" + position);
				view = View.inflate(getApplicationContext(),
						R.layout.list_callsmssafe_item, null);
				// Ѱ�Һ��ӵĹ��� ��һ���ȽϺ�ʱ ������Դ�Ĳ��� ��һ�����Ż���
				// û�б�Ҫÿһ�ζ�ȥ�鿴ÿ�����ӵ����� ����id�õ����ӵ����á�
				// ֻ��Ҫ�ں��ӳ�����ʱ�� �� �ҵ����� ����������������
				holder = new ViewHolder();// ����һ�����±� ��¼���ӵ���Ϣ��
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder); // �Ѻ��ӵ����� ���±� ���ڿڴ���
			}

			BlackNumberInfo info = infos.get(position);
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("�绰����");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("�绰+��������");
			}
			holder.tv_number.setText(info.getNumber());
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BlackNumberInfo info = infos.get(position);
					String number = info.getNumber();
					dao.delete(number);//ɾ�����ݿ�����ļ�¼��
					infos.remove(info);//ɾ����ǰ�����Ӧ�ļ��ϵ����ݡ�
					adapter.notifyDataSetChanged();//֪ͨ����ˢ�¡�
					totalCount--;
				}
			});
			
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}

	/**
	 * ���һ������������ �����Ի���
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder buidler = new Builder(this);
		final AlertDialog dialog = buidler.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		dialog.setView(contentView, 0, 0, 0, 0);
		final EditText et_blacknumber = (EditText) contentView
				.findViewById(R.id.et_blacknumber);
		final RadioGroup rg_mode = (RadioGroup) contentView
				.findViewById(R.id.rg_mode);
		Button bt_ok = (Button) contentView.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) contentView.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(blacknumber)) {
					Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 0).show();
					return;
				}
				int id = rg_mode.getCheckedRadioButtonId();
				String mode = "3";
				switch (id) {
				case R.id.rb_all:
					mode ="3";
					break;
				case R.id.rb_phone:
					mode ="1";
					break;
				case R.id.rb_sms:
					mode = "2";
					break;
				}
				dao.add(blacknumber, mode);//���ص����ݿ� �� 
				totalCount++;
				dialog.dismiss();
				//ˢ�½��档
				BlackNumberInfo blacknumberInfo = new BlackNumberInfo();
				blacknumberInfo.setMode(mode);
				blacknumberInfo.setNumber(blacknumber);
				//���µĺ�������Ϣ���뵽��ǰ����ļ�������
				infos.add(0, blacknumberInfo);
				adapter.notifyDataSetChanged();//֪ͨ�������
			}
		});
		dialog.show();
	}
}
