package com.erichlotto.showshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
	ArrayList<Artista>artistasChecados;

	public ConcertInfo(Service ctx) {
		handler = new Handler();
		artistasChecados = new ArrayList<Artista>();
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
		long check_interval_in_millis = 86400000; //wait 1 day before checking again
		for(Artista a:artistasChecados){
			if(a.timestamp+check_interval_in_millis<System.currentTimeMillis())
				artistasChecados.remove(a);
			if(a.artist.equals(artist))return;
		}
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
			JSONArray resultados;
			char c = string.charAt(19);
			System.out.println(c);
			if(c=='{'){//Pegamos um objeto, e nao um array
				System.out.println("OBJETO");
//				resultados=new JSONArray(response.getJSONObject("events").getJSONObject("event"));
				resultados=new JSONArray();
				resultados.put(response.getJSONObject("events").getJSONObject("event"));
			}else{
			resultados = response.getJSONObject("events")
					.getJSONArray("event");
			}
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

	private void trataNotificacao(final double smallestDistance, final String id, final String artist, final String event, final String venue, final String date, final String url) {
		artistasChecados.add(new Artista(artist, System.currentTimeMillis()));
		System.out.println("checou "+artist+" id="+id);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		int defaultMaxDistance = ctx.getResources().getInteger(R.integer.max_distance);
		long storedMaxDistance = sharedPref.getInt("STORED_MAX_DIST", defaultMaxDistance);
//		System.out.println(storedMaxDistance);

		if(smallestDistance>storedMaxDistance*1000 || SavedData.isIdAndArtistStored(ctx, id, artist))return;
		
		/* PEGAMOS A IMAGEM DO ARTISTA */
		
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				// code to do the HTTP request
				try {
					URL url = new URL(
							"http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
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
							System.out.println("CARREGOU");
							try {
								JSONObject response = new JSONObject(result);
								String imgURL = response.getJSONObject("artist")
										.getJSONArray("image").getJSONObject(3).getString("#text");
								System.out.println(imgURL);
								URL bandUrl = new URL(imgURL);
								Bitmap srcBmp = BitmapFactory.decodeStream(bandUrl.openConnection().getInputStream());
								Bitmap dstBmp;
								if (srcBmp.getWidth() >= srcBmp.getHeight()){

									  dstBmp = Bitmap.createBitmap(
									     srcBmp, 
									     srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
									     0,
									     srcBmp.getHeight(), 
									     srcBmp.getHeight()
									     );

									}else{

									  dstBmp = Bitmap.createBitmap(
									     srcBmp,
									     0, 
									     srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
									     srcBmp.getWidth(),
									     srcBmp.getWidth() 
									     );
									}
								
								
								mostraNotificacao(smallestDistance, id, artist, event, venue, date, url, dstBmp);

							} catch (JSONException e) {
								Bitmap image = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher);
								mostraNotificacao(smallestDistance, id, artist, event, venue, date, url, image);
							}

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
	
	private void mostraNotificacao(double smallestDistance, String id, String artist, String event, String venue, String date, String url, Bitmap image){
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
		        .setLargeIcon(image)
				.setStyle(new NotificationCompat.BigTextStyle().bigText("Evento: "+event+"\nData: "+date+"\nLocal: "+venue+" (a "+Math.round(smallestDistance/1000)+" km)"))
		        .setContentIntent(contentIntent)
				.setContentText(date);
		NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(Math.round(SystemClock.uptimeMillis()/1000), mBuilder.build());
		SavedData.storeIdAndArtist(ctx, id, artist);
	}

	private float calculaDistancia(Location loc1, Location loc2) {
		float[] results = new float[1];
		Location.distanceBetween(loc1.getLatitude(), loc1.getLongitude(),
				loc2.getLatitude(), loc2.getLongitude(), results);
		return results[0];
	}
	
	class Artista{
		String artist;
		long timestamp;
		public Artista(String artist2, long currentTimeMillis) {
			this.artist = artist2;
			this.timestamp = currentTimeMillis;
		}
	}

}
