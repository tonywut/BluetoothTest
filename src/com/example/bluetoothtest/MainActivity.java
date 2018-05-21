package com.example.bluetoothtest;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
	private String LOG_TAG = "BluetoothTest";
	private Button mBtnStartRecordMIC;
	private Button mBtnStartRecordBTMIC;
	private Button mBtnStopRecord;
	private Button mBtnStartPlay;
	private Button mBtnStopPlay;
	private Button mBtnHytPlay;
	private Context mContext;

	private MediaRecorder mediaRecorder;
	private File audioFile;
	private TextView mTv_progress;
	// private RecordAmplitude amplitude;
	// private boolean isRecording = false;
	private MediaPlayer mediaPlayer;
	private AudioManager mAudioManager = null;
	File path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButtonListener b = new ButtonListener();
		mBtnStartRecordMIC = (Button) findViewById(R.id.btn_startrecordMIC);
		mBtnStartRecordMIC.setOnClickListener(b);
		mBtnStartRecordMIC.setOnTouchListener(b);

		mBtnStartRecordBTMIC = (Button) findViewById(R.id.btn_startrecordBTMIC);
		mBtnStartRecordBTMIC.setOnClickListener(b);
		mBtnStartRecordBTMIC.setOnTouchListener(b);

		mBtnStopRecord = (Button) findViewById(R.id.btn_stoprecord);
		mBtnStopRecord.setOnClickListener(b);
		mBtnStopRecord.setOnTouchListener(b);

		mBtnStartPlay = (Button) findViewById(R.id.btn_startplay);
		mBtnStartPlay.setOnClickListener(b);
		mBtnStartPlay.setOnTouchListener(b);

		mBtnStopPlay = (Button) findViewById(R.id.btn_stopplay);
		mBtnStopPlay.setOnClickListener(b);
		mBtnStopPlay.setOnTouchListener(b);

		mBtnHytPlay = (Button) findViewById(R.id.btn_hytplay);
		mBtnHytPlay.setOnClickListener(b);
		mBtnHytPlay.setOnTouchListener(b);

		mTv_progress = (TextView) findViewById(R.id.tv_progress);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnPreparedListener(this);

		mContext = getApplicationContext();

		if (mAudioManager == null)
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mediaRecorder = new MediaRecorder();
		// amplitude = new RecordAmplitude();

		path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recordertest/");
		path.mkdirs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void stopRecorder() {
		if (mAudioManager.isBluetoothScoOn()) {
			mAudioManager.setBluetoothScoOn(false);
			mAudioManager.stopBluetoothSco();
		}
		// isRecording = false;
		// amplitude.cancel(true);

		mediaRecorder.stop();
		// mediaRecorder.release();
	}

	class ButtonListener implements OnClickListener, OnTouchListener {
		public void onClick(View v) {
			if (v.getId() == R.id.btn_startrecordMIC) {
				// stopRecorder();
				recorder_Media(false);
			}
			if (v.getId() == R.id.btn_startrecordBTMIC) {
				// stopRecorder();
				recorder_Media(true);
			}
			if (v.getId() == R.id.btn_stoprecord) {
				stopRecorder();
			}
			if (v.getId() == R.id.btn_startplay) {
				mediaPlayer.reset();
				try {
					mediaPlayer.setDataSource(audioFile.getAbsolutePath());
					mediaPlayer.setLooping(true);
					mediaPlayer.prepare();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (v.getId() == R.id.btn_hytplay) {
				mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				mAudioManager.startBluetoothSco();
				mAudioManager.setBluetoothScoOn(true); 
				mAudioManager.setSpeakerphoneOn(false);
				mediaPlayer.reset();
				try {
					mediaPlayer.setDataSource(audioFile.getAbsolutePath());
					mediaPlayer.setLooping(true);
					mediaPlayer.prepare();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (v.getId() == R.id.btn_stopplay) {
				if (mAudioManager.isBluetoothScoOn()) {
					mAudioManager.setBluetoothScoOn(false);
					mAudioManager.stopBluetoothSco();
				}
				mediaPlayer.stop();
			}
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	boolean iskeydown = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(LOG_TAG, "keyCode:" + keyCode + "KeyEvent:" + event.toString());
		switch (keyCode) {
		case 142:
			if (iskeydown == false) {
				recorder_Media(true);
			}
			iskeydown = true;
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(LOG_TAG, "keyCode:" + keyCode + "KeyEvent:" + event.toString());
		switch (keyCode) {
		case 142:
			iskeydown = false;
			stopRecorder();
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void recorder_Media(boolean useBTMIC) {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			audioFile = File.createTempFile("recording", ".3gp", path);
			mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
			mediaRecorder.prepare();
			// mediaRecorder.start();

			if (useBTMIC && getheadsetStatus() == 2) {
				if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
					Log.e(LOG_TAG, "数值++开启录音的问题。。系统不支持蓝牙录音");
					return;
				}
				// Request audio focus for playback，获得临时焦点的问题。
				int result = mAudioManager.requestAudioFocus(afChangeListener,
						// Use the music stream.
						AudioManager.STREAM_MUSIC, // Request permanent focus.
						AudioManager.AUDIOFOCUS_GAIN);
				// mAudioManager.setBluetoothScoOn(true);
				// mAudioManager.setMicrophoneMute(false);

				// 蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
				// 也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
				mContext.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
						Log.e(LOG_TAG, "state" + state);
						if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
							// 蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
							// LogUtil.e("sco_time:"+(System.currentTimeMillis() - lastSCO));
							mAudioManager.setBluetoothScoOn(true);
							mAudioManager.setMicrophoneMute(false);
							Log.e(LOG_TAG, "数值++开启录音的问题。。开启SCO连接");
							// mRecorder.start();// 开始录音
							// mediaRecorder.start();
							context.unregisterReceiver(this); // 别遗漏
						}
					}
				}, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
				mAudioManager.startBluetoothSco();
				Log.e(LOG_TAG, "数值++~~~~开启录音的问题。。开始SCO连接");
				mediaRecorder.start();// 开始录音
			} else {
				mediaRecorder.start();
			}
			// isRecording = true;
			// mediaRecorder.start();
			// amplitude.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class RecordAmplitude extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (!isCancelled()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				publishProgress(mediaRecorder.getMaxAmplitude());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			mTv_progress.setText(values[0] + "");
		}
	}

	AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				Log.e(LOG_TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

				Log.e(LOG_TAG, "AUDIOFOCUS_GAIN");
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				mAudioManager.abandonAudioFocus(afChangeListener);
				Log.e(LOG_TAG, "AUDIOFOCUS_LOSS");
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				Log.e(LOG_TAG, "AUDIOFOCUS_REQUEST_GRANTED");
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {

				Log.e(LOG_TAG, "AUDIOFOCUS_REQUEST_FAILED");

			}
		}
	};

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub

		mediaPlayer.start();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
	}

	// return: 1为有线耳机 2为蓝牙耳机
	public int getheadsetStatus() {

		if (mAudioManager.isWiredHeadsetOn()) {
			return 1;
		} else {
			Toast.makeText(MainActivity.this, "耳机不ok", Toast.LENGTH_SHORT).show();
		}

		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

		// int isBlueCon;//蓝牙适配器是否存在，即是否发生了错误
		if (ba == null) {
			// isBlueCon = -1; //error
			return -1;
		} else if (ba.isEnabled()) {
			int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP); // 可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
			int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET); // 蓝牙头戴式耳机，支持语音输入输出
			int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH); // 蓝牙穿戴式设备

			// 查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
			int flag = -1;
			if (a2dp == BluetoothProfile.STATE_CONNECTED) {
				flag = a2dp;
			} else if (headset == BluetoothProfile.STATE_CONNECTED) {
				flag = headset;
			} else if (health == BluetoothProfile.STATE_CONNECTED) {
				flag = health;
			}
			// 说明连接上了三种设备的一种
			if (flag != -1) {
				// isBlueCon = 1; //connected
				return 2;
			}
		}
		return -2;
	}

}
