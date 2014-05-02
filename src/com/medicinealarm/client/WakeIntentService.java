package com.medicinealarm.client;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public abstract class WakeIntentService extends IntentService {

	abstract void doReminderWork(Intent intent);
	
	public static final String LOCK_NAME_STATIC = "com.medicinealarm.client_wake_lock";
	private static PowerManager.WakeLock lockStatic = null;
	
	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}
	
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if(lockStatic == null) {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lockStatic = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		
		return (lockStatic);
	}
	
	public WakeIntentService(String name) {
		super(name);
	}
	
	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doReminderWork(intent);
		} finally {
			synchronized (getLock(this)){
				if(getLock(this) != null) {
					Log.v("WakeIntentService", "Releasing wakelock");
					try {
						getLock(this).release();
					}catch (Throwable th){
						
					}
				} else {
					Log.e("WakeIntentService", "Wakelock reference is null");
				}

			}
		}
	}
	

}
