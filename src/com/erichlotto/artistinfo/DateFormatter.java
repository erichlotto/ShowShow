package com.erichlotto.artistinfo;

import java.util.HashMap;
import java.util.Map;

public class DateFormatter {
	public static String format(String s){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Sun", "Dom");
		map.put("Mon", "Seg");
		map.put("Tue", "Ter");
		map.put("Wed", "Qua");
		map.put("Thu", "Qui");
		map.put("Fri", "Sex");
		map.put("Sat", "Sab");
		
		String weekDay = s.split(" ")[0].split(",")[0];
		String monthDay = s.split(" ")[1];
		String month = s.split(" ")[2];
		String year = s.split(" ")[3];
		String time = s.split(" ")[4].split(":")[0]+":"+s.split(" ")[4].split(":")[1];
		return map.get(weekDay)+", "+monthDay+"/"+month+"/"+year+" as "+time;
	}
}
