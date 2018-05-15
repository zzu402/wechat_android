package com.hzz.wechat;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import com.hzz.utils.GsonUtils;
import com.hzz.utils.LogUtils;
import com.hzz.utils.StringUtil;
import com.hzz.websocket.WebSocketClientImpl;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText userName;
	private EditText secretKey;
	private Activity mActivity;
	//先定义 
	private SharedPreferences sp;

	@SuppressLint("HandlerLeak") private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String jsonStr = (String) msg.obj;
			Map<String, Object> verifyMap = GsonUtils.jsonToMap(jsonStr);
			String code = (String) verifyMap.get("code");
			if (!StringUtil.isBlank(code)) {
				if (code.equals("success")) {
					Intent intent=new Intent(mActivity, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(mActivity,(String) verifyMap.get("errorMsg"),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity=this;
		sp=  getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		setContentView(R.layout.activity_login);
		userName = (EditText) findViewById(R.id.userName);
		secretKey = (EditText) findViewById(R.id.sercretKey);
		userName.setText(sp.getString("userName", ""));
		secretKey.setText(sp.getString("secretKey", ""));
	}

	/*
	 * 点击登陆按钮
	 */
	public void login(View v) {
		final String name = userName.getText().toString().trim();
		final String key = secretKey.getText().toString().trim();
		if (StringUtil.isBlank(name)) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (StringUtil.isBlank(key)) {
			Toast.makeText(this, "请输入密钥", Toast.LENGTH_SHORT).show();
			return;
		}
		/*
		 保存登陆信息
		 */
		Editor editor=sp.edit();
		editor.putString("userName", name);
		editor.putString("secretKey", key);
		editor.commit();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String uri = String.format("%s/%s",
						WebSocketClientImpl.baseUrl, name);
				try {
					WebSocketClientImpl wSocketClientImpl = WebSocketClientImpl
							.getAvailableSocketClient(uri);
					wSocketClientImpl.setHandler(mHandler);
					wSocketClientImpl.connect(wSocketClientImpl);
					Map<String, Object> verifyMap = new HashMap<String, Object>();
					verifyMap.put("secretkey", key);
					wSocketClientImpl.send(GsonUtils.toJson(verifyMap));
				} catch (URISyntaxException e) {
					LogUtils.error(getClass(), "登陆失败", e);
				}

			}
		}).start();

	}

}
