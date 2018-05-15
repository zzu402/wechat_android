package com.hzz.utils;

import android.util.Log;

public class LogUtils {

	private static boolean debugger = true;

	@SuppressWarnings("rawtypes")
	public static void info(Class clazz, String message) {
		if (debugger) {
			Log.i(clazz.getName(), message);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void error(Class clazz, String message, Throwable tr) {
		if (debugger) {
			if (tr != null)
				Log.e(clazz.getName(), message, tr);
			else{
				Log.e(clazz.getName(), message);
			}
		}
	}

}
