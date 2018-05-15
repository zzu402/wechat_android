package com.hzz.utils;
import android.graphics.PointF;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用root权限执行Linux下的Shell指令
 */
public class RootShellCmd {
	private OutputStream suOS;

	/**
	 * 执行shell指令
	 *
	 * @param cmd
	 *            指令
	 */
	public final void execSU(String cmd) {
		try {
			if (suOS == null) {
				suOS = Runtime.getRuntime().exec("su").getOutputStream();
			}
			suOS.write(cmd.getBytes());
			suOS.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void checkRoot() throws IOException {
		Runtime.getRuntime().exec("su");
	}


	public final void exec(String cmd) {
		try {
            Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 后台模拟全局按键
	 *
	 * @param start
	 * @param end
	 * @param duration
	 */
	public final void simulateJump(PointF start, PointF end, int duration) {
		execSU("input swipe " + start.x + " " + start.y + " " + end.x + " " + end.y + " " + duration + "\n");
	}
	public final void touch(int x, int y) {
		execSU("input tap "+x+" "+y + "\n");
	}
	
	public final void input(String text) {
		execSU("input text "+text + "\n");
	}
	public final void del() {
		execSU("input keyevent 67 "+ "\n");
	}
	
	
	
	

    /**
     * 后台模拟全局按键
     *
     * @param keyCode
     */
    public final void simulateKey(int keyCode) {
        execSU("input keyevent " + keyCode + "\n");
    }

    public final void screenCapture(String path) {
		execSU("screencap -p " + path + "\n");
	}
}
