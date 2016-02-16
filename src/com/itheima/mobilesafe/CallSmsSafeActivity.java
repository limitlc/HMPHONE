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
	 * 加载数据的开始位置
	 */
	private int startIndex = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter == null) {
				adapter = new CallSmsSafeAdapter();
				lv_call_sms_safe.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();// 通知数据适配器更新下界面。
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
			// 滚动状态变化了。
			// 静止-->滚动
			// 滚动-->静止
			// 手指触摸滚动-->惯性滑动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 静止状态
					// 判断界面是否已经滑动到了最后面。
					// 获取最后一个可见条目的位置
					int position = lv_call_sms_safe.getLastVisiblePosition(); // 19
					int total = infos.size(); // 20
					if (isloading) {
						Toast.makeText(getApplicationContext(),
								"正在加载数据，不要重复刷新。", 0).show();
						return;
					}
					if (position >= (total - 5)) {

						// 指定新的获取数据的开始位置
						startIndex += 20;
						if (startIndex >= totalCount) {
							Toast.makeText(getApplicationContext(),
									"没有更多数据了，别再拖了", 0).show();
							return;
						}
						Toast.makeText(getApplicationContext(),
								"拖动到最下面了。加载更多数据", 0).show();
						fillData();
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 惯性滑动

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸滚动

					break;
				}

			}

			// 滚动的时候执行的方法
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
				} else {// 原来集合里面已经有数据了。
					infos.addAll(dao.findPart(startIndex));
				}
				// 发送一个消息 更新界面。
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
			// 为了减少view对象创建的个数 使用历史缓存的view对象 convertview
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				Log.i(TAG, "使用历史缓存的view对象" + position);
				holder = (ViewHolder) view.getTag();
			} else {
				Log.i(TAG, "创建新的view对象" + position);
				view = View.inflate(getApplicationContext(),
						R.layout.list_callsmssafe_item, null);
				// 寻找孩子的过程 是一个比较耗时 消耗资源的操作 进一步的优化。
				// 没有必要每一次都去查看每个孩子的特征 根据id得到孩子的引用。
				// 只需要在孩子出生的时候 ， 找到特征 ，把特征给存起来
				holder = new ViewHolder();// 买了一个记事本 记录孩子的信息。
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder); // 把孩子的引用 记事本 放在口袋里
			}

			BlackNumberInfo info = infos.get(position);
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("电话+短信拦截");
			}
			holder.tv_number.setText(info.getNumber());
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BlackNumberInfo info = infos.get(position);
					String number = info.getNumber();
					dao.delete(number);//删除数据库里面的记录。
					infos.remove(info);//删除当前界面对应的集合的数据。
					adapter.notifyDataSetChanged();//通知界面刷新。
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
	 * 添加一条黑名单号码 弹出对话框
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
					Toast.makeText(getApplicationContext(), "号码不能为空", 0).show();
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
				dao.add(blacknumber, mode);//加载到数据库 。 
				totalCount++;
				dialog.dismiss();
				//刷新界面。
				BlackNumberInfo blacknumberInfo = new BlackNumberInfo();
				blacknumberInfo.setMode(mode);
				blacknumberInfo.setNumber(blacknumber);
				//把新的黑名单信息加入到当前界面的集合里面
				infos.add(0, blacknumberInfo);
				adapter.notifyDataSetChanged();//通知界面更新
			}
		});
		dialog.show();
	}
}
