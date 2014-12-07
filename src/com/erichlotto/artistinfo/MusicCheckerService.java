package com.erichlotto.artistinfo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MusicCheckerService extends Service{
	
    private NotificationManager notificationManager;
    private Context ctx;

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Notificação ativada");
		this.ctx=getApplicationContext();
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		registerReceiver(mReceiver, iF);
		
	    return Service.START_STICKY;
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
				

				NotificationCompat.Builder mBuilder =
					    new NotificationCompat.Builder(ctx)
					    .setSmallIcon(R.drawable.ic_launcher)
					    .setContentTitle("Show de "+artist)
					    .setContentText("Em algum lugar perto de você");
				
				NotificationManager mNotifyMgr = 
				        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNotifyMgr.notify(0, mBuilder.build());
				
			}
		};
	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }
}
