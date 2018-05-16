package com.hzz.utils;

import java.io.File;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.wechat.queue.MqManager;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("NewApi")
public class OcrUtils {

	private static   String AK = "dG3dFA4Gw5yEG5DXngG2xFxt";
	private static  String SK = "l65P32d4zybHigZVDAFbLpaHYY7w3qY6";
	private static Context mContext = null;


	public static void setAK(String aK) {
		AK = aK;
	}

	public static void setSK(String sK) {
		SK = sK;
	}

	public static void setContext(Context context) {
		mContext = context;
	}

	public static String ocr(String imagePath, int model) {

		return baiduOcr(imagePath);

	}

	public static synchronized String baiduOcr(final String imagePath) {
		OCR.getInstance().initAccessTokenWithAkSk(
				new OnResultListener<AccessToken>() {
					@Override
					public void onResult(AccessToken result) {
						OCR.getInstance().initWithToken(mContext, result);
						GeneralParams param = new GeneralParams();
						param.setImageFile(new File(imagePath));
						OCR.getInstance().recognizeGeneralBasic(param,
								new OnResultListener<GeneralResult>() {
									@Override
									public void onResult(GeneralResult result) {
										try {
											MqManager
													.getMq(String
															.format("OCR_RESULT"))
													.push(GsonUtils.toJson(result
															.getJsonRes()));
										} catch (InterruptedException e) {
											LogUtils.error(OcrUtils.class,
													"enter queue error", e);
										}

									}

									@Override
									public void onError(OCRError ocrError) {
										try {
											MqManager
													.getMq(String
															.format("OCR_RESULT"))
													.push(GsonUtils.toJson(ocrError
															.getMessage()));
										} catch (InterruptedException e) {
											LogUtils.error(OcrUtils.class,
													"enter queue error", e);
										}
									}
								});
					}

					@Override
					public void onError(OCRError error) {
						LogUtils.error(OcrUtils.class, "init ocr error", error);
					}
				}, mContext, AK, SK);

		String json = "";
		try {
			json = MqManager.getMq(String.format("OCR_RESULT")).pop();
		} catch (InterruptedException e) {
			return "";
		}
		return json;
	}
	
	public static boolean checkOcrResult(String text,int model){
		if(text.contains("words_result")){
			return true;
		}
		return false;
	}

}
