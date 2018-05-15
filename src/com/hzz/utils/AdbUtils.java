package com.hzz.utils;

import com.hzz.constant.GlobalConstant;

public class AdbUtils {

	
	public static RootShellCmd shellCmd=new RootShellCmd();
	
	public static void touch(int x,int y){
		shellCmd.touch(x, y);
	}
	
	public static void inputText(String text){
		shellCmd.input(text);
	}
	
	public static void printScreen(){
		shellCmd.screenCapture(GlobalConstant.SCREENSHOT_LOCATION);
	}
	
	public static void del(){
		shellCmd.del();
	}
	
	
	
	
	
}
