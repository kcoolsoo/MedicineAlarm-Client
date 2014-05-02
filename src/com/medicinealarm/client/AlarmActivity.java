package com.medicinealarm.client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

import com.medicinealarm.model.MedicineType;
import com.medicinealarm.model.MedicineTypeList;
import com.medicinealarm.model.PatientInfo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmActivity extends Activity {

	static final String TAG = "com.medicinealarm.client.AlarmActivity";

	private MediaPlayer mMediaPlayer;
	private Uri mUri;
	private int mRingerMode;
	private Vibrator mVibrator = null;
	private long[] mPattern = { 0, 1000, 0, 0, 1000, 0 };
	private String mTime;
	private String mUsername;
	private String mPassword;
	private String mTypeId;

	private DataRetrieveTask mDataRetrieveTask = null;
	private RecordingTask mRecordingTask = null;
	
	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "AuthenticValue";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.activity_alarm);
		
		Context context = getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				PRIVATE_MODE);

		mUsername = pref.getString(KEY_USERNAME, null);
		mPassword = pref.getString(KEY_PASSWORD, null);

		// mUri = getIntent().getParcelableExtra("RINGTONE_URI");
		// mRingerMode = getIntent().getIntExtra("RINGER_MODE",
		// AudioManager.RINGER_MODE_NORMAL);
		
		// playSound(this, mUri);
		
		Button btnAlarmOntime = (Button) findViewById(R.id.button_alarm_ontime);
		Button btnAlarmDelayed = (Button) findViewById(R.id.button_alarm_delayed);
		Button btnAlarmNo = (Button) findViewById(R.id.button_alarm_no);

		btnAlarmOntime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// if(mMediaPlayer.isPlaying())
				// mMediaPlayer.stop();
				if (mVibrator != null)
					mVibrator.cancel();
				String[] records = {"10", "ontime", mTypeId};				
				showPointsDialog("Conglatulations! You got 10 points!", records);
			}
		});
		
		btnAlarmDelayed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mVibrator != null)
					mVibrator.cancel();
				String[] records = {"5", "delayed", mTypeId};
				showPointsDialog("Conglatulations! You got 5 points!", records);
			}
		});

		btnAlarmNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				// if(mMediaPlayer.isPlaying())
				// mMediaPlayer.stop();
				if (mVibrator != null)
					mVibrator.cancel();
				String[] records = {"10", "missed",mTypeId};
				showPointsDialog("Sorry. You did not get any points.", records);
			}
		});

	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		mDataRetrieveTask = new DataRetrieveTask();
		mDataRetrieveTask.execute((Void) null);
		
	}

	private void showPointsDialog(CharSequence message, final String[] records) {
		// Dialog for clicking stop button
		final AlertDialog pointsDialog = new AlertDialog.Builder(this).create();
		pointsDialog.setTitle("Reward Points");
		pointsDialog.setMessage(message);
		pointsDialog.setButton(RESULT_OK, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// add 10 points into database
						mRecordingTask = new RecordingTask();
						mRecordingTask.execute(records);
						
						finish();
					}
				});
		
		pointsDialog.show();
	}

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// audioManager.setRingerMode(mRingerMode);

			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0
					&& mRingerMode == AudioManager.RINGER_MODE_NORMAL) {
				Log.v("AlarmActivity", "Melody");
				// audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
				// audioManager.getStreamVolume(AudioManager.STREAM_ALARM),
				// AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE +
				// AudioManager.FLAG_ALLOW_RINGER_MODES);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(true);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			} else if (mRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
				Log.v("AlarmActivity", "Vibrate");
				mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				mVibrator.vibrate(mPattern, 1);
			} else if (mRingerMode == AudioManager.RINGER_MODE_NORMAL
					+ AudioManager.RINGER_MODE_VIBRATE) {
				Log.v("AlarmActivity", "Vibrate + Melody");
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(true);
				mMediaPlayer.prepare();

				mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

				mMediaPlayer.start();
				mVibrator.vibrate(mPattern, 1);
			} else {
				Log.v("AlarmActivity", "Slient");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class DataRetrieveTask extends AsyncTask<Void, Void, MedicineTypeList> {

		@Override 
		protected void onPreExecute(){
			Intent intent = getIntent();
			mTime = intent.getStringExtra("CUR_TIME");

		}
		
		@Override
		protected MedicineTypeList doInBackground(Void... params) {
			final String url = getString(R.string.base_uri) + "/medicinetype?time="
					+ mTime;

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					mUsername, mPassword);
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
				ResponseEntity<MedicineTypeList> responseEntity = restTemplate
						.exchange(url, HttpMethod.GET, requestEntity, MedicineTypeList.class);

				return responseEntity.getBody();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MedicineTypeList medicineTypeList) {
			TextView tvMedicineName = (TextView) findViewById(R.id.textView_alarm_medicine_name);
			TextView tvMedicineTime = (TextView) findViewById(R.id.textView_alarm_medicine_time);
			
			String time = changeTimeFormat(mTime);
			tvMedicineTime.setText(time);
			
			String strMedicineType = "";
			for(int i = 0; i < medicineTypeList.getMedicineList().size(); i++) {
				MedicineType medicineType = new MedicineType();
				medicineType = medicineTypeList.getMedicineList().get(i);
				if(i > 0){
					strMedicineType += "\n"; 
				}				
				strMedicineType += medicineType.getType();
				mTypeId = Integer.toString(medicineType.getId());
			}
			//Toast.makeText(getApplicationContext(), mTypeId, Toast.LENGTH_LONG).show();
			//strMedicineType += "\nSomething";
			Log.i(TAG, strMedicineType);
			tvMedicineName.setText(strMedicineType);
		}
		
	}

	public class RecordingTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			final String url = getString(R.string.base_uri) + "/records?points="
					+ params[0] + "&record=" + params[1] + "&typeId=" + params[2] + "&time=" + mTime;
			
			HttpAuthentication authHeader = new HttpBasicAuthentication(
					mUsername, mPassword);
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
				ResponseEntity<PatientInfo> responseEntity = restTemplate
						.exchange(url, HttpMethod.GET, requestEntity, null);

				responseEntity.getBody();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {

		}
	}
	
	private String changeTimeFormat(String time) {
		DateFormat oldFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat newFormat = new SimpleDateFormat("hh:mm a");
	
		try {
			Date date = oldFormat.parse(time);
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			time = newFormat.format(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return time;
	}

}
