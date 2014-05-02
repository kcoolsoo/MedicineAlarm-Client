package com.medicinealarm.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("BootReceiver", "BEFORE GET INTO THE BootReceiver.");
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i("BootReceiver", "GET INTO THE BootReceiver.");

			Intent i = new Intent(context, AlarmInitService.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
		}

	}
}
