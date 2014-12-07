package com.erichlotto.artistinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ViewedConcerts {
	
	private static String KEY = "VIEWED_IDS";
	
	public static boolean isStored(Context ctx, String string){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String s = sharedPref.getString(KEY, "");
		return s.contains(string);
	}
	
	public static boolean store(Context ctx, String string){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(KEY, ","+string+",");
		return editor.commit();
	}

}
