package com.hg.util;

public class DateFormat {

	public static String getHMS(long time) {
		long hours = time / (1000 * 60 * 60);
		long minutes = (time - (hours * (1000 * 60 * 60))) / (1000 * 60);
		long second = (time - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
		String diffTime = "";
		if (minutes < 10) {
			diffTime = hours + "h" + minutes + "m" + second + "s";
		} else {
			diffTime = hours + "h" + minutes + "m" + second + "s";
		}
		return diffTime;
	}

}
