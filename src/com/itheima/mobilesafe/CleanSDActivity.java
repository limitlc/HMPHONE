package com.itheima.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class CleanSDActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file = Environment.getExternalStorageDirectory();
		File[] files = file.listFiles();
		for(File f: files){
			String name = f.getName();
			if(f.isDirectory()){
				System.out.println(name);
				//��ѯ����ļ��������Ƿ������ݿ�������� ������� ��ʾ�û����ļ���ɾ���ˡ�
			}
		}
	}
}
