package com.medicinealarm.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.medicinealarm.client.PatientInfoActivity.DataRetrieveTask;
import com.medicinealarm.model.PatientInfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

	static final String TAG = "com.medicinealarm.client.AlarmReceiver";
	
	private Uri mUri = null;
	private int mRingerMode;
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");	
		Calendar calNow = Calendar.getInstance();	
		calNow.setTimeInMillis(System.currentTimeMillis());
		String time = dateFormat.format(calNow.getTime());
		Log.i(TAG, time);
		
		//mUri = intent.getParcelableExtra("RINGTONE");
		//mRingerMode = intent.getIntExtra("RINGER_MODE", AudioManager.RINGER_MODE_NORMAL);
		Uri uri = intent.getParcelableExtra("RINGTONE");
		int ringerMode = intent.getIntExtra("RINGER_MODE", AudioManager.RINGER_MODE_NORMAL);
		WakeIntentService.acquireStaticLock(context);
		
		Intent service = new Intent(context, AlarmService.class);
		service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		service.putExtra("RINGTONE_URI", uri);
		service.putExtra("RINGER_MODE", ringerMode);
		service.putExtra("CUR_TIME", time);
		
		context.startService(service);
		

//		Intent fireAlarm = new Intent(context, AlarmActivity.class);
//		fireAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//		fireAlarm.putExtra("RINGTONE_URI", mUri);
//		fireAlarm.putExtra("RINGER_MODE", mRingerMode);
//		context.startActivity(fireAlarm);
	}
	

	
	public void setAlarms(Context context, ArrayList<Calendar> targetCals, Uri uri, int ringerMode) {

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
				
		for(int i = 0; i < targetCals.size(); i++) {
			Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
			//Log.i(TAG, (String) uri.toString());
			intent.putExtra("RINGTONE", uri);
			intent.putExtra("RINGER_MODE", ringerMode);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context.getApplicationContext(), i, intent, PendingIntent.FLAG_ONE_SHOT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, targetCals.get(i).getTimeInMillis(),
					pendingIntent);
			
			intentArray.add(pendingIntent);
		}
		
	}

	public Calendar setTimeToTakeMedicine(int hour, int minute, int second) {

		Calendar calNow = Calendar.getInstance();
		calNow.setTimeInMillis(System.currentTimeMillis());

		Calendar calSet = (Calendar) calNow.clone();

//		calSet.set(Calendar.SECOND, calSet.get(Calendar.SECOND) + 15);

		 calSet.set(Calendar.HOUR_OF_DAY, hour);
		 calSet.set(Calendar.MINUTE, minute);
		 calSet.set(Calendar.SECOND, second);
		 calSet.set(Calendar.MILLISECOND, 0);

		if (calSet.compareTo(calNow) <= 0) {
			// Today set time passed count to tomorrow
			calSet.add(Calendar.DATE, 1);
		}

		return calSet;
	}
}
