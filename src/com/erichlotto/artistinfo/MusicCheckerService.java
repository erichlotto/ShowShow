package com.erichlotto.artistinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MusicCheckerService extends Service {

	String artist;
	String album;
	String track;
	private Context ctx;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("MusicCheckerService iniciado");
		this.ctx = getApplicationContext();
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
			artist = intent.getStringExtra("artist");
			album = intent.getStringExtra("album");
			track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					ctx).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Show de " + artist)
					.setContentText("Em algum lugar perto de você");

			NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotifyMgr.notify(0, mBuilder.build());
			
			
			
			
			
			/* CHECAMOS SE HÁ EVENTOS PARA O ARTISTA */

			Thread trd = new Thread(new Runnable() {
				@Override
				public void run() {
					// code to do the HTTP request
					try {
						URL url = new URL(
								"http://ws.audioscrobbler.com/2.0/?method=artist.getevents&artist="+URLEncoder.encode(artist, "UTF-8")+"&api_key=4a7e24f35563d05d6b8283ba766afb78&format=json");
						HttpURLConnection con = (HttpURLConnection) url
								.openConnection();
						readStream(con.getInputStream());
					} catch (Exception e) {
					}
				}

				private void readStream(InputStream in) {
					String result = "";
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new InputStreamReader(in));
						String line = "";
						while ((line = reader.readLine()) != null) {
							result += line;
						}
					} catch (IOException e) {
						System.out.println(e.toString());
					} finally {
						if (reader != null) {
							try {
								reader.close();
								jsonParse(result);
							} catch (IOException e) {
								System.out.println(e.toString());
							}
						}
					}
				}
			});
			trd.start();

		}
	};
	
	private void jsonParse(String string){
		JSONObject response;
		try {
			response = new JSONObject(string);
			JSONArray resultados = response.getJSONObject("events").getJSONArray("event");
			for(int i=0; i<resultados.length(); i++){
				String venue = resultados.getJSONObject(i).getJSONObject("venue").getString("name");
				String date = DateFormatter.format(resultados.getJSONObject(i).getString("startDate"));
				System.out.println("Show em "+venue+" no dia "+date);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}
	
	private double calculaDistancia(){
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
