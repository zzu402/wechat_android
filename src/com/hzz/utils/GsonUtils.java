package com.hzz.utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {

	private static Gson gson = new Gson();

	public static String toJson(Object o) {
		String json = gson.toJson(o);
		return json;
	}

	public static Map<String,Object> jsonToMap(String json) {
		Map<String, Object> map = gson.fromJson(json,
				new TypeToken<Map<String, Object>>() {
				}.getType());
		return map;
	}

}
