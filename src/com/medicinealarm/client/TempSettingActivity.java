package com.medicinealarm.client;

import java.util.ArrayList;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class TempSettingActivity extends ListActivity {

	public static final int NUMBER_OF_ITEM = 3;
	public static final int RINGTONE_CODE = 100;

	private Uri mUri = null;

	private static final String LOG_TAG = TempSettingActivity.class.getSimpleName();

	private String mAlarmType = "type";
	private String mAlarmTone = "tone";

	private SettingListAdapter mAdapter;

	private LayoutInflater mInflater;
	private View mView;
	
	private int mRingerMode;
	private int mRadioSelected = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp_setting);

		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.setting_type_item, null);

		RingtoneManager.setActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_RINGTONE, mUri);

		mUri = getIntent().getParcelableExtra(
				RingtoneManager.EXTRA_RINGTONE_EXISTING_URI);
		mRingerMode = getIntent().getIntExtra("RINGER_MODE", AudioManager.RINGER_MODE_NORMAL);

		mAdapter = new SettingListAdapter();
		mAdapter.addTitleItme("Alarm type");
		mAdapter.addTitleItme("Alarm tone");

		mAdapter.addSubTitleItem(mAlarmType);
		mAdapter.addSubTitleItem(mAlarmTone);

		setListAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_setting_save:
			Intent returnIntent = new Intent();
			returnIntent.putExtra("RINGTONE_CHANGED", mUri);
			returnIntent.putExtra("RINGER_MODE", mRingerMode);
			setResult(RESULT_OK, returnIntent);

			finish();
			return true;
		case R.id.action_setting_cancel:
			mAdapter.getAudioManger().setStreamVolume(
					AudioManager.STREAM_ALARM, mAdapter.getSeekBarProgress(),
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		int checkedItem;
			
		switch (position) {
		case 0:		
			switch(mRingerMode) {
			case AudioManager.RINGER_MODE_NORMAL:
				checkedItem = 0;
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				checkedItem = 1;
				break;
			case AudioManager.RINGER_MODE_NORMAL + AudioManager.RINGER_MODE_VIBRATE:
				checkedItem = 2;
				break;
			default:
				checkedItem = 0;
				break;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.alarmtype)
					.setSingleChoiceItems(R.array.alarmtypes, checkedItem,
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										mRadioSelected = which;										
										break;
									case 1:
										mRadioSelected = which;										
										break;
									case 2:
										mRadioSelected = which;
										
										break;
									default:
										break;
									}
								}
							})
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch(mRadioSelected) {
									case 0:
										mRingerMode = AudioManager.RINGER_MODE_NORMAL;
										break;
									case 1:
										mRingerMode = AudioManager.RINGER_MODE_VIBRATE;
										break;
									case 2:
										mRingerMode = AudioManager.RINGER_MODE_NORMAL + AudioManager.RINGER_MODE_VIBRATE;
										break;
									default:
										break;
									}


								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							});
			AlertDialog alarmTypeDialog = builder.create();
			alarmTypeDialog.show();
			break;
		case 1:
			Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			i.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mUri);
			this.startActivityForResult(i, RINGTONE_CODE);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RINGTONE_CODE:
				mUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (mUri != null) {
					// view update
					Ringtone ringtone = RingtoneManager.getRingtone(this, mUri);
					mAlarmTone = ringtone.getTitle(this);
					Log.v("SettingActivity", mAlarmTone);
					mView.invalidate();
				}
				break;
			default:
				break;
			}
		}

	}

	private class SettingListAdapter extends BaseAdapter implements
			SeekBar.OnSeekBarChangeListener {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEEKBAR = 1;
		private static final int TYPE_TOTAL_COUNT = TYPE_SEEKBAR + 1;

		private ArrayList<String> mTitleData = new ArrayList<String>();
		private ArrayList<String> mSubTitleData = new ArrayList<String>();
		private LayoutInflater mInflater;

		private AudioManager mAudioManager;
		private ViewHolder mHolder;
		private int mSeekBarProgress;

		public SettingListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}

		public void addTitleItme(final String title) {
			mTitleData.add(title);
		}

		public void addSubTitleItem(final String subTitle) {
			mSubTitleData.add(subTitle);
		}

		public AudioManager getAudioManger() {
			return mAudioManager;
		}

		public int getSeekBarProgress() {
			return mSeekBarProgress;
		}

		@Override
		public int getItemViewType(int position) {
			if (position != 2)
				return TYPE_ITEM;
			else
				return TYPE_SEEKBAR;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_TOTAL_COUNT;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTitleData.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTitleData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			mHolder = null;
			int type = getItemViewType(position);
			Log.v(LOG_TAG, "getView " + position + " " + convertView
					+ " type = " + type);
			if (convertView == null) {
				mHolder = new ViewHolder();
				switch (type) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.setting_type_item,
							null);
					mHolder.mTextTitle = (TextView) convertView
							.findViewById(R.id.textView_setting_text);
					mHolder.mTextSubTitle = (TextView) convertView
							.findViewById(R.id.textView_setting_subtext);
					mHolder.mImageNext = (ImageView) convertView
							.findViewById(R.id.imageView_next);
					mHolder.mTextTitle.setText(mTitleData.get(position));
					mHolder.mTextSubTitle.setText(mSubTitleData.get(position));
					break;
				case TYPE_SEEKBAR:
					convertView = mInflater.inflate(
							R.layout.setting_type_seekbar, null);
					mHolder.mImageVolume = (ImageView) convertView
							.findViewById(R.id.imageView_volume);
					mHolder.mSeekBar = (SeekBar) convertView
							.findViewById(R.id.seekBar_volume);
					mHolder.mSeekBar.setMax(mAudioManager
							.getStreamMaxVolume(AudioManager.STREAM_ALARM));
					mSeekBarProgress = mAudioManager
							.getStreamVolume(AudioManager.STREAM_ALARM);
					mHolder.mSeekBar.setProgress(mAudioManager
							.getStreamVolume(AudioManager.STREAM_ALARM));
					mHolder.mSeekBar.setTag(position);
					mHolder.mSeekBar.setOnSeekBarChangeListener(this);
					break;
				default:
					break;
				}
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress,
					AudioManager.FLAG_PLAY_SOUND);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}

	public static class ViewHolder {
		public TextView mTextTitle;
		public TextView mTextSubTitle;
		public ImageView mImageNext;
		public ImageView mImageVolume;
		public SeekBar mSeekBar;
	}

}
