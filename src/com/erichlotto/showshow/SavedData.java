package com.erichlotto.showshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavedData {
	
	private static String KEY = "VIEWED_IDS";
	
	public static boolean isIdStored(Context ctx, String string){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String s = sharedPref.getString(KEY, "");
		return s.contains(string);
	}
	
	public static boolean storeId(Context ctx, String string){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sharedPref.edit();
		if(!isIdStored(ctx,string))editor.putString(KEY, ","+string+",");
		return editor.commit();
	}
	
	public static int getStoredMaxDistance(Context ctx){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		int defaultMaxDistance = ctx.getResources().getInteger(R.integer.max_distance);
		return sharedPref.getInt("STORED_MAX_DIST", defaultMaxDistance);
	}
	
	public static boolean storeMaxDistance(Context ctx, int prog){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("STORED_MAX_DIST", prog);
		return editor.commit();
	}

	public static boolean getStoredArtistDetailsFlag(Context ctx){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean defaultValue = ctx.getResources().getBoolean(R.bool.artist_curiosities);
		return sharedPref.getBoolean("ARTIST_CURIOSITIES", defaultValue);
	}
	
	public static boolean setStoredArtistDetailsFlag(Context ctx, boolean b){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("ARTIST_CURIOSITIES", b);
		return editor.commit();
	}

	public static boolean getStoredMusicDetailsFlag(Context ctx){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean defaultValue = ctx.getResources().getBoolean(R.bool.music_curiosities);
		return sharedPref.getBoolean("MUSIC_CURIOSITIES", defaultValue);
	}
	
	public static boolean setStoredMusicDetailsFlag(Context ctx, boolean b){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("MUSIC_CURIOSITIES", b);
		return editor.commit();
	}

}
