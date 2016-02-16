package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onreceiver");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("onUpdate");
		Intent i  = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		System.out.println("onDeleted");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		Intent i  = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		System.out.println("onDisabled");
		Intent i  = new Intent(context,UpdateWidgetService.class);
		context.stopService(i);
		super.onDisabled(context);
	}
	
}
