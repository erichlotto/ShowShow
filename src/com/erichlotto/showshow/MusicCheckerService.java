package com.erichlotto.showshow;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MusicCheckerService extends Service {

	String artist;
	String album;
	String track;
	Handler handler;
	Runnable updateData;
	ConcertInfo ci;
	BandInfo bi;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("MusicCheckerService iniciado");
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		registerReceiver(mReceiver, iF);
		ci = new ConcertInfo(this);
		bi = new BandInfo(getApplicationContext());
		return Service.START_STICKY;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			if(intent.hasExtra("artist"))artist = intent.getStringExtra("artist");
			if(intent.hasExtra("album"))album = intent.getStringExtra("album");
			if(intent.hasExtra("track"))track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);			
	        if(intent.hasExtra("playing")) {
	            if(intent.getBooleanExtra("playing", false)) {
	                System.out.println("TOCANDO");
	            } else {
	                System.out.println("PARADO");
	            }
	        }

			/* CHECAMOS SE HÁ EVENTOS PARA O ARTISTA */
			ci.check(artist);
			
			/* CHECAMOS A INFORMAÇÃO DA BANDA */
			bi.check(artist);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
