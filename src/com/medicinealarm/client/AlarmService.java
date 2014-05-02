package com.medicinealarm.client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends WakeIntentService {

	static final String TAG = "com.medicinealarm.client.AlarmService";

	private Uri mUri = null;
	private int mRingerMode;

	public AlarmService() {
		super("");
		// TODO Auto-generated constructor stub
	}

	@Override
	void doReminderWork(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Doing work.1");

		mUri = intent.getParcelableExtra("RINGTONE_URI");
		mRingerMode = intent.getIntExtra("RINGER_MODE",
				AudioManager.RINGER_MODE_NORMAL);
		//Log.i(TAG, mUri.toString());
		//Log.i(TAG, Integer.toString(mRingerMode));
		
		String currentTime = intent.getStringExtra("CUR_TIME");
		//Log.i(TAG, currentTime);
		long[] pattern = { 10, 100, 1000, 10, 100, 1000, 100 };
		
		// Intent fireAlarm = new Intent(getApplication(), AlarmActivity.class);
		// fireAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		// fireAlarm.putExtra("RINGTONE_URI", mUri);
		// fireAlarm.putExtra("RINGER_MODE", mRingerMode);
		// getApplication().startActivity(fireAlarm);

		NotificationCompat.Builder builder = 
				new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher_main)
				.setContentTitle("Medicine Alarm System")
				.setContentText("It's time to take medicine.")
				.setLights(Color.parseColor("red"), 1000, 2000)
				.setVibrate(pattern)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setAutoCancel(true);
			
		Intent notifyIntent = new Intent(getApplicationContext(), AlarmActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		notifyIntent.putExtra("RINGTONE_URI", mUri);
		notifyIntent.putExtra("RINGER_MODE", mRingerMode);
		notifyIntent.putExtra("CUR_TIME", currentTime);
		
		long notifyId = System.currentTimeMillis();

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(contentIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		if(notifyNum < 9) {
//			notifyNum += 1;
//		} else {
//			notifyNum = 0;
//		}
		
		notificationManager.notify((int) notifyId, builder.build());
		//

	}

	// public void onAttachedToWindow() {
	// Window window = getWindow();
	// window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	// | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	// | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	// | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	// }

}
