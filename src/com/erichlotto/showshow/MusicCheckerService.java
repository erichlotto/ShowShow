package com.erichlotto.showshow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MusicCheckerService extends Service {

	String artist;
	String album;
	String track;
	Handler handler;
	Runnable updateData;
	ConcertInfo ci;
	BandInfo bi;
	TrackInfo ti;
	ToastManager toastMan;
	int previousStoredMaxDist;//Precisamos verificar se o usuario alterou esse valor, para checar novamente os shows.

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		previousStoredMaxDist=SavedData.getStoredMaxDistance(this);
		System.out.println("MusicCheckerService iniciado");
		toastMan = new ToastManager(getApplicationContext());
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		registerReceiver(mReceiver, iF);
		ci = new ConcertInfo(this);
		bi = new BandInfo(getApplicationContext(), toastMan);
		ti = new TrackInfo(getApplicationContext(), toastMan);
		return Service.START_STICKY;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(previousStoredMaxDist != SavedData.getStoredMaxDistance(context)){
				previousStoredMaxDist=SavedData.getStoredMaxDistance(context);
				ci.artistasChecados.clear();
			}
			toastMan.limpaFrases();
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			if(intent.hasExtra("artist"))artist = intent.getStringExtra("artist");
			if(intent.hasExtra("album"))album = intent.getStringExtra("album");
			if(intent.hasExtra("track"))track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);			
	        if(intent.hasExtra("playing")) {
	            if(intent.getBooleanExtra("playing", false)) {
	            	toastMan.start();
//	            	System.out.println("TOCANDO");
	            } else {
	            	toastMan.stop();
//	                System.out.println("PARADO");
	            }
	        }
	        
	        
			/* CHECAMOS SE HÁ EVENTOS PARA O ARTISTA */
	        	ci.check(artist);
			
			/* CHECAMOS A INFORMAÇÃO DA BANDA */
		    if(SavedData.getStoredArtistDetailsFlag(context))
	        	bi.check(artist);
			
			/* CHECAMOS A INFORMAÇÃO DA MUSICA */
	        if(SavedData.getStoredMusicDetailsFlag(context))
	        	ti.check(artist,track);
	        
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
