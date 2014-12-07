package com.erichlotto.showshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class TrackInfo {

	//private Location myLocation;
	Handler handler;
	Runnable updateData;
	Context ctx;
	String infos[];
	int index_infos;
	private ToastManager toastMan;

	public TrackInfo(Context con, ToastManager toastMan) {
		this.toastMan = toastMan;
		this.ctx = con;
		this.index_infos = 0;
		this.infos = new String[]{};
	}

	public void check(final String artist,final String track) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				// code to do the HTTP request
				try {
					URL url = new URL(
							"http://ws.audioscrobbler.com/2.0/?method=track.getInfo&artist="
									+ URLEncoder.encode(artist, "UTF-8")
									+ "&track=" + URLEncoder.encode(track, "UTF-8") + "&api_key=4a7e24f35563d05d6b8283ba766afb78&format=json");
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
							jsonParseConcert(result);
						} catch (IOException e) {
							System.out.println(e.toString());
						}
					}
				}
			}
		});
		trd.start();
	}
	
	private void jsonParseConcert(String string) {
		JSONObject response;
		try {
			response = new JSONObject(string);
			JSONObject wiki = response.getJSONObject("track").getJSONObject("wiki");
			
			Document doc = Jsoup.parse(wiki.getString("content"));
			infos = doc.text().split("\\. ");
			toastMan.adicionaFrases(infos);
			
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}



}
