package com.erichlotto.artistinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		registerReceiver(mReceiver, iF);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
