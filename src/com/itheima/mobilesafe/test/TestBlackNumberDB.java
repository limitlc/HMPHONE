package com.itheima.mobilesafe.test;

import java.util.Random;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				this.getContext());
		helper.getWritableDatabase();
	}

	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		//13512340000~13512340099
		Random random = new Random();
		long basenumber = 13512340000l;
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
		}
	}

	public void testDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}

	public void testUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}

	
	
	
	public void testFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}

	public void testFindMode() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		String mode = dao.findMode("110");
		System.out.println("À¹½ØÄ£Ê½£º" + mode);
	}
}
