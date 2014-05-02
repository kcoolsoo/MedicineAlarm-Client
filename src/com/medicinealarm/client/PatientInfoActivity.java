package com.medicinealarm.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.medicinealarm.client.R;
import com.medicinealarm.model.MedicineTimeList;
import com.medicinealarm.model.PatientInfo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PatientInfoActivity extends Activity {

	static final String TAG = "com.medicinealarm.client.MainActivity";

	private DataRetrieveTask mTask = null;

	TextView mTextViewName;
	TextView mTextViewPoints;
	TextView mTextViewMedicine;

	Calendar mCalTimeToTake;

	final static int RQS_1 = 1;
	private int SETTING_RESULT_CODE = 2;

	int mHour = 0;
	int mMinute = 0;
	int mSecond = 0;

	String mMedicineTimeList = "";

	private Uri mUri = getAlarmUri();
	private int mRingerMode = AudioManager.RINGER_MODE_NORMAL;
	private ArrayList<Calendar> mAlarmTimes;

	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "AuthenticValue";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_info);

		mTextViewName = (TextView) findViewById(R.id.textView_main_name);
		mTextViewPoints = (TextView) findViewById(R.id.textView_main_points);
		mTextViewMedicine = (TextView) findViewById(R.id.textView_main_medicine);

		// mTextViewName.setPaintFlags(mTextViewName.getPaintFlags()
		// | Paint.UNDERLINE_TEXT_FLAG);
		// mTextViewPoints.setPaintFlags(mTextViewPoints.getPaintFlags()
		// | Paint.UNDERLINE_TEXT_FLAG);
		mTextViewMedicine.setPaintFlags(mTextViewPoints.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_info, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		mTask = new DataRetrieveTask();
		mTask.execute((Void) null);

		mTextViewMedicine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PatientInfoActivity.this,
						MedicineTimeListActivity.class);
				intent.putExtra("TIME_LIST", mMedicineTimeList);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingIntent = new Intent(this, TempSettingActivity.class);
			settingIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					mUri);
			settingIntent.putExtra("RINGER_MODE", mRingerMode);
			startActivityForResult(settingIntent, SETTING_RESULT_CODE);

			// Intent intent = new Intent(getApplicationContext(),
			// SettingActivity.class);
			// startActivityForResult(intent, SETTING_RESULT_CODE);
			return true;
		case R.id.action_sign_out:
			createDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
				ResponseEntity<PatientInfo> responseEntity = restTemplate
						.exchange(url, HttpMethod.GET, requestEntity,
								PatientInfo.class);

				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(PatientInfo patientInfo) {
			mTextViewName.setText(patientInfo.getName().toString() + ".");
			mTextViewPoints.setText(Integer.toString(patientInfo.getPoints())
					+ ".");
			
			// sorting times with medicine types
			List<MedicineTimeList> medicineTimeList = new ArrayList<MedicineTimeList>();
			for(int i = 0; i < patientInfo.getTimeList().size(); i ++) {
				medicineTimeList.add(new MedicineTimeList(patientInfo.getTypeList().get(i), patientInfo.getTimeList().get(i).toString()));
			}
			Collections.sort(medicineTimeList);
			
			String types = "";
			List<String> typeList = new ArrayList<String>();
			int typeLength = patientInfo.getTypeList().size();
			for (int i = 0; i < typeLength; i++) {
				String type = patientInfo.getTypeList().get(i);

				if (i == 0) {
					types += Integer.toString(i + 1) + ". " + type + "\n";
					typeList.add(type);
				} else if (typeLength > 1) {
					for (int j = 0; j < i; j++) {
						if (type.equals(patientInfo.getTypeList().get(j))) {
							break;
						} else {
							types += Integer.toString(i + 1) + ". " + type
									+ ".\n";
							typeList.add(type);
						}
					}
				}
			}
			mTextViewMedicine.setText(types);
			
			mMedicineTimeList = "";
			for(int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				mMedicineTimeList += Integer.toString(i + 1) + ". " + type + ": ";
				for(int j = 0; j < medicineTimeList.size(); j++) {
					if(type.equals(medicineTimeList.get(j).getType())) {
						String time = medicineTimeList.get(j).getTime();
						time = changeTimeFormat(time);
						mMedicineTimeList += time + " ";
					}
				}
				mMedicineTimeList += "\n";
			}
			
			Intent i = new Intent(PatientInfoActivity.this,
					AlarmInitService.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(i);

			// mAlarmTimes = new ArrayList<Calendar>();
			// for(int i = 0; i < patientInfo.getTimeList().size(); i++) {
			// Calendar c = new GregorianCalendar();
			// c.setTime(patientInfo.getTimeList().get(i));
			//
			// int hour = c.get(Calendar.HOUR_OF_DAY);
			// int minute = c.get(Calendar.MINUTE);
			// int second = c.get(Calendar.SECOND);
			//
			// c = setTimeToTakeMedicine(hour, minute, second);
			//
			// mAlarmTimes.add(c);
			// }
			//
			// setAlarms(mAlarmTimes);
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

	private Calendar setTimeToTakeMedicine(int hour, int minute, int second) {

		Calendar calNow = Calendar.getInstance();
		calNow.setTimeInMillis(System.currentTimeMillis());

		Calendar calSet = (Calendar) calNow.clone();

		calSet.set(Calendar.SECOND, calSet.get(Calendar.SECOND) + 5);

		// calSet.set(Calendar.HOUR_OF_DAY, hour);
		// calSet.set(Calendar.MINUTE, minute);
		// calSet.set(Calendar.SECOND, second);
		// calSet.set(Calendar.MILLISECOND, 0);

		if (calSet.compareTo(calNow) <= 0) {
			// Today set time passed count to tomorrow
			calSet.add(Calendar.DATE, 1);
		}

		return calSet;
	}

	private void setAlarms(ArrayList<Calendar> targetCals) {

		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

		for (int i = 0; i < targetCals.size(); i++) {
			Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
			intent.putExtra("RINGTONE", mUri);
			intent.putExtra("RINGER_MODE", mRingerMode);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getBaseContext(), i, intent, PendingIntent.FLAG_ONE_SHOT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, targetCals.get(i)
					.getTimeInMillis(), pendingIntent);

			intentArray.add(pendingIntent);
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

	public void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sing out")
				.setMessage("Are you sure you want to sign out?")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								signOut();
								finish();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

		Dialog dialog = builder.create();
		dialog.show();
	}

	public void signOut() {
		Context context = this.getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				PRIVATE_MODE);

		SharedPreferences.Editor editor = pref.edit();
		editor.remove(KEY_USERNAME);
		editor.remove(KEY_PASSWORD);
		editor.clear();
		editor.commit();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// return results from order activity
		if (requestCode == SETTING_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				mUri = data.getParcelableExtra("RINGTONE_CHANGED");
				RingtoneManager.setActualDefaultRingtoneUri(this,
						RingtoneManager.TYPE_RINGTONE, mUri);
				mRingerMode = data.getIntExtra("RINGER_MODE",
						AudioManager.RINGER_MODE_NORMAL);

			} else if (resultCode == RESULT_CANCELED) {

			}
		}
		// mCalTimeToTake = setTimeToTakeMedicine(mHour, mMinute, mSecond);
		// setAlarm(mCalTimeToTake);
	}
}
