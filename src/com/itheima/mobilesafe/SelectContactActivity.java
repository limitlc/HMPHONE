package com.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	private ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv = (ListView) findViewById(R.id.lv_select_contact);
		final List<Map<String, String>> data = getContactInfos();
		lv.setAdapter(new SimpleAdapter(this, data, R.layout.list_contact_item,
				new String[]{"name","phone"}, new int[]{R.id.tv_name,R.id.tv_phone}));
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = 	data.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
			}
		});
	}
	
	/**
	 * 获取系统的所有的联系人信息.
	 * 
	 * @return
	 */
	public List<Map<String, String>> getContactInfos() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		// 查询raw_contact表 取联系人id
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			System.out.println("联系人的id为:" + id);
			if (id != null) {
				Map<String, String> map = new HashMap<String, String>();
				// 查询data表 把当前联系人的数据给取出来.
				Cursor dataCursor = resolver.query(datauri, null,
						"raw_contact_id=?", new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(dataCursor
							.getColumnIndex("data1"));
					String mimetype = dataCursor.getString(dataCursor
							.getColumnIndex("mimetype"));
					if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
						System.out.println("电话:" + data1);
						map.put("phone", data1);
					} else if ("vnd.android.cursor.item/email_v2"
							.equals(mimetype)) {
						System.out.println("邮件地址:" + data1);
						map.put("email", data1);
					} else if ("vnd.android.cursor.item/name".equals(mimetype)) {
						System.out.println("姓名:" + data1);
						map.put("name", data1);
					}
				}
				data.add(map);
				dataCursor.close();
			}
		}
		cursor.close();
		return data;
	}
}
