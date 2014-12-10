package com.erichlotto.showshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class ConcertInfo {

	private Location myLocation;
	Handler handler;
	Runnable updateData;
	Context ctx;

	public ConcertInfo(Service ctx) {
		handler = new Handler();
		updateData = new Runnable() {
			public void run() {
				getPosition();
				handler.postDelayed(updateData, 300000);
			}
		};
		handler.post(updateData);
		this.ctx = ctx;
	}

	public void check(final String artist) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				// code to do the HTTP request
				try {
					URL url = new URL(
							"http://ws.audioscrobbler.com/2.0/?method=artist.getevents&artist="
									+ URLEncoder.encode(artist, "UTF-8")
									+ "&api_key=4a7e24f35563d05d6b8283ba766afb78&format=json");
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
							jsonParseConcert(result, artist);
						} catch (IOException e) {
							System.out.println(e.toString());
						}
					}
				}
			}
		});
		trd.start();
	}

	private void getPosition() {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				myLocation = location;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	private void jsonParseConcert(String string, String artist) {
		JSONObject response;
		try {
			response = new JSONObject(string);
			JSONArray resultados = response.getJSONObject("events")
					.getJSONArray("event");
			double smallestDistance =-1;
			String event = "";
			String venue = "";
			String date = "";
			String url = "";
			String id = "";
			for (int i = 0; i < resultados.length(); i++) {
				JSONObject current = resultados.getJSONObject(i);
				String _event = current.getString("title");
				String _id = current.getString("id");
				String _venue = current.getJSONObject("venue").getString("name");
				String _date = DateFormatter.format(current.getString("startDate"));
				String _url = current.getJSONObject("venue").getString("website");
				Location concertLocation = new Location("");
				String strLatitude = current.getJSONObject("venue").getJSONObject("location").getJSONObject("geo:point").getString("geo:lat");
				String strLongitude = current.getJSONObject("venue").getJSONObject("location").getJSONObject("geo:point").getString("geo:long");
				if (!strLatitude.equals("") && !strLongitude.equals("")) {
					double latitude = Double.parseDouble(strLatitude);
					double longitude = Double.parseDouble(strLongitude);
					concertLocation.setLatitude(latitude);
					concertLocation.setLongitude(longitude);
					double distancia = calculaDistancia(myLocation, concertLocation);
					if(distancia<smallestDistance || smallestDistance==-1){
						smallestDistance=distancia;
						event = _event;
						venue = _venue;
						date = _date;
						url = _url;
						id = _id;
					}
				}
			}
			trataNotificacao(Math.round(smallestDistance), id, artist, event, venue, date, url);
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}

	private void trataNotificacao(double smallestDistance, String id, String artist, String event, String venue, String date, String url) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		int defaultMaxDistance = ctx.getResources().getInteger(R.integer.max_distance);
		long storedMaxDistance = sharedPref.getInt("STORED_MAX_DIST", defaultMaxDistance);
		System.out.println(storedMaxDistance);
		if(smallestDistance>storedMaxDistance*1000 || SavedData.isIdStored(ctx, id))return;
		String encodedURL="";
		try {
			encodedURL = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.equals("")?"http://www.google.com.br/?#q="+encodedURL:url));
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ctx).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Show de " + artist)
		        .setSmallIcon(R.drawable.notification_small_icon)
		        .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher))
				.setStyle(new NotificationCompat.BigTextStyle().bigText("Evento: "+event+"\nData: "+date+"\nLocal: "+venue+"\n(a "+Math.round(smallestDistance/1000)+" km)"))
		        .setContentIntent(contentIntent)
				.setContentText(date);
		NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(Math.round(SystemClock.uptimeMillis()/1000), mBuilder.build());
		SavedData.storeId(ctx, id);
	}

	private float calculaDistancia(Location loc1, Location loc2) {
		float[] results = new float[1];
		Location.distanceBetween(loc1.getLatitude(), loc1.getLongitude(),
				loc2.getLatitude(), loc2.getLongitude(), results);
		return results[0];
	}

}
