package com.hzz.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.hzz.utils.GsonUtils;
import com.hzz.utils.StringUtil;
import com.wechat.queue.MqManager;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/3
 */
public class WebSocketClientImpl extends WebSocketClient {

	public static String baseUrl = "ws://121.54.168.163:8080/websocket";

	private static WebSocketClientImpl socketClient = null;

	private Handler mHandler;

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	private WebSocketClientImpl(URI serverUri) {
		super(serverUri, new Draft_6455());
	}

	private WebSocketClientImpl(URI serverUri, Draft protocolDraft) {
		super(serverUri, protocolDraft);
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		System.out.println("onOpen");
	}

	@Override
	public void onMessage(String s) {
		System.out.println("onMessage:" + s);
		Map<String, Object> verifyMap = GsonUtils.jsonToMap(s);
		String phone = (String) verifyMap.get("verifyPhone");
		String verifyCode = (String) verifyMap.get("verifyCode");

		String code = (String) verifyMap.get("code");
		if (!StringUtil.isBlank(code)) {
			Message message = mHandler.obtainMessage();
			message.what = 1;
			message.obj = s;
			mHandler.sendMessage(message);
		}
		 if (!StringUtil.isBlank(phone) && !StringUtil.isBlank(verifyCode)) {
	            try {
	                MqManager.getMq(String.format("VERIFY_FRIEND")).push(GsonUtils.toJson(verifyMap));
	            } catch (InterruptedException e) {
	               e.printStackTrace();
	            }
	        }
		
	}

	@Override
	public void onClose(int i, String s, boolean b) {
		System.out.println("onClose");
	}

	@Override
	public void onError(Exception e) {
		System.out.println("onError");

	}

	public static void keepClientAlive(final WebSocketClientImpl client) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (client.isClosed()) {
						client.connect();
					}
				}
			}
		}).start();
	}

	public void connect(WebSocketClientImpl client) {

		if (client != null && !client.isOpen()) {
			client.connect();
			while (!client.getReadyState().equals(READYSTATE.OPEN)) {
				SystemClock.sleep(500);
			}
		}

	}

	public static WebSocketClientImpl getAvailableSocketClient(String uri)
			throws URISyntaxException {
		if (socketClient == null) {
			synchronized (WebSocketClientImpl.class) {
				if (socketClient == null)
					socketClient = new WebSocketClientImpl(new URI(uri));
			}
		}
		return socketClient;
	}

	public static WebSocketClientImpl getSocketClient() {
		return socketClient;
	}

}
