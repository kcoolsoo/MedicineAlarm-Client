package com.medicinealarm.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.medicinealarm.model.PatientInfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class AlarmInitService extends Service {

	static final String TAG = "com.medicinealarm.client.AlarmInitService";

	AlarmReceiver alarm = new AlarmReceiver();
	
	private DataRetrieveTask mTask = null;
	
	private Uri mUri;
	private int mRingerMode;
	
	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "AuthenticValue";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	
	public void onCreate() {
		super.onCreate();
		
		long firstTime = SystemClock.elapsedRealtime();
		long repeatTime = 60 * 1000;
		PendingIntent pSender = PendingIntent.getService(AlarmInitService.this, 0, new Intent(AlarmInitService.this, AlarmInitService.class), 0);
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, repeatTime, pSender);		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int statId) {
		mUri = getAlarmUri();
		mRingerMode = AudioManager.RINGER_MODE_NORMAL;
		
		mTask = new DataRetrieveTask();
		mTask.execute((Void) null);
		
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class DataRetrieveTask extends AsyncTask<Void, Void, PatientInfo> {

		@Override
		protected PatientInfo doInBackground(Void... params) {
			Context context = getApplicationContext();
			SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
					PRIVATE_MODE);

			String username = pref.getString(KEY_USERNAME, null);
			String password = pref.getString(KEY_PASSWORD, null);

			final String url = getString(R.string.base_uri) + "/main";
			// final String url = getString(R.string.base_uri) + "/main?id=" + username;

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAuthorization(authHeader);

			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJacksonHttpMessageConverter());
			try {
				Log.i(TAG, "doInBackground()");
				ResponseEntity<PatientInfo> responseEntity = restTemplate.exchange(
						url, HttpMethod.GET, requestEntity, PatientInfo.class);
				
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(PatientInfo patientInfo) {
				
			ArrayList<Calendar> alarmTimes = new ArrayList<Calendar>();

			for(int i = 0; i < patientInfo.getTimeList().size(); i++) {
				Calendar c = new GregorianCalendar();
				c.setTime(patientInfo.getTimeList().get(i));
				
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				
				c = alarm.setTimeToTakeMedicine(hour, minute, second);
				
				alarmTimes.add(c);
			}	
			
			alarm.setAlarms(getApplicationContext(), alarmTimes, mUri, mRingerMode);
		}
	}
	
	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}
}
