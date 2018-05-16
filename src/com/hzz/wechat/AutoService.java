package com.hzz.wechat;

import java.util.Map;
import com.hzz.utils.AdbUtils;
import com.hzz.utils.AutoScript;
import com.hzz.utils.GsonUtils;
import com.hzz.utils.LogUtils;
import com.hzz.utils.OcrUtils;
import com.hzz.websocket.WebSocketClientImpl;
import com.wechat.queue.MqManager;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
public class AutoService extends Service {
	public static final String ACTION = "action";
	public static final String SHOW = "show";
	public static final String HIDE = "hide";
	public static final int SHOWINFO = 101;

	private FloatingView mFloatingView;
	private Thread mTask;
	private AutoRunnable mRun;
	private Context context;

	public interface AutoObserver {
		void setDisplayInfo(String text);
	}

	private class AutoRunnable implements Runnable {
		@SuppressWarnings("unused")
		private boolean mPause = false;
		private boolean mStop = false;

		void resume() {
			mPause = false;
		}

		void pause() {
			mPause = true;
		}

		void stop() {
			mStop = true;
		}

		@Override
		public void run() {
			OcrUtils.setContext(context);
			while (!mStop) {
				String json;
				LogUtils.info(getClass(), "Auto service is running...");
				try {
					AdbUtils.printScreen();
					json = MqManager.getMq(String.format("VERIFY_FRIEND"))
							.pop();
					Map<String, Object> verifyMap = GsonUtils.jsonToMap(json);
					String phone = (String) verifyMap.get("verifyPhone");
					String verifyCode = (String) verifyMap.get("verifyCode");
					Double userId=(Double) verifyMap.get("userId");
					Double verifyInfoId=(Double) verifyMap.get("verifyInfoId");
					verifyMap.put("userId", userId.intValue());
					verifyMap.put("verifyInfoId", verifyInfoId.intValue());
					Toast.makeText(context, "接收到："+phone+" 的验证请求，准备执行验证脚本。", Toast.LENGTH_LONG).show();
					int result = AutoScript.autoRun(phone, verifyCode);
					AutoScript.goHome();
					verifyMap.put("resultCode", "error");
					if (result == 1) {
						verifyMap.put("resultCode", "success");
					} else if (result == 2) {
						verifyMap.put("errorMsg",
								"enter add friend view failure");
					} else if (result == 3) {
						verifyMap.put("errorMsg", "no find add or send button");
					} else if (result == 4) {
						verifyMap.put("errorMsg", "send msg failure");
					}else if(result==5){
						verifyMap.put("errorMsg", "ocr error");
					}
					LogUtils.info(getClass(), "auto run result is :"+result);
					WebSocketClientImpl wClientImpl = WebSocketClientImpl
							.getSocketClient();
					if (wClientImpl.isOpen()) {
						wClientImpl.send(GsonUtils.toJson(verifyMap));
						LogUtils.info(getClass(), "auto run result send  success... ");
					}
				} catch (InterruptedException e) {
					LogUtils.error(getClass(), "auto run error",e);
				}

			}
		}
	};

	public void start() {
		// start
		if (mTask == null) {
			mRun = new AutoRunnable();
			mTask = new Thread(mRun);
			mTask.start();
			// resume
		} else if (mRun != null) {
			mRun.resume();
		}
	}

	public void stop() {
		// stop
		mRun.stop();
		mTask = null;
		mRun = null;
	}

	public void pause() {
		mRun.pause();
	}

	@Override
	public void onCreate() {
		context = this;
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mFloatingView = new FloatingView(this);
		if (intent != null) {
			String action = intent.getStringExtra(ACTION);
			if (SHOW.equals(action)) {
				mFloatingView.show();
			} else if (HIDE.equals(action)) {
				mFloatingView.hide();
			}
		}

		start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (mFloatingView != null) {
			mFloatingView = null;
		}
		stop();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
