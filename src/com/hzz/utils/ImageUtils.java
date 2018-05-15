package com.hzz.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/28
 */
public class ImageUtils {

	/*
	 * 图片裁切
	 * 
	 * @param x1 选择区域左上角的x坐标
	 * 
	 * @param y1 选择区域左上角的y坐标
	 * 
	 * @param width 选择区域的宽度
	 * 
	 * @param height 选择区域的高度
	 * 
	 * @param sourcePath 源图片路径
	 * 
	 * @param descpath 裁切后图片的保存路径
	 */
	public static void cron(int x1, int y1, int width, int height,
			String sourcePath, String descpath) {

		Bitmap bitmap = getBitmapFromPath(sourcePath);
		Bitmap desc = Bitmap.createBitmap(bitmap, x1, y1, width, height);

		File f = new File(descpath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			desc.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static boolean isSimilarity(String sourcePath, String targetPath) {
		Bitmap bm1 = getBitmapFromPath(sourcePath);
		Bitmap bm2 = getBitmapFromPath(targetPath);
		int s=similarity(bm1, bm2);
		if(s>90)
			return true;
		
		return false;
	}

	public static int similarity(Bitmap b, Bitmap viewBt) {

		int t = 0;
		int f = 0;
		// 把图片转换为Bitmap
		Bitmap bm_one = b;
		Bitmap bm_two = viewBt;
		// 保存图片所有像素个数的数组，图片宽×高
		int[] pixels_one = new int[bm_one.getWidth() * bm_one.getHeight()];
		int[] pixels_two = new int[bm_two.getWidth() * bm_two.getHeight()];
		// 获取每个像素的RGB值
		bm_one.getPixels(pixels_one, 0, bm_one.getWidth(), 0, 0,
				bm_one.getWidth(), bm_one.getHeight());
		bm_two.getPixels(pixels_two, 0, bm_two.getWidth(), 0, 0,
				bm_two.getWidth(), bm_two.getHeight());
		// 如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
		if (pixels_one.length >= pixels_two.length) {
			// 对每一个像素的RGB值进行比较
			for (int i = 0; i < pixels_two.length; i++) {
				int clr_one = pixels_one[i];
				int clr_two = pixels_two[i];
				// RGB值一样就加一（以便算百分比）
				if (clr_one == clr_two) {
					t++;
				} else {
					f++;
				}
			}
		} else {
			for (int i = 0; i < pixels_one.length; i++) {
				int clr_one = pixels_one[i];
				int clr_two = pixels_two[i];
				if (clr_one == clr_two) {
					t++;
				} else {
					f++;
				}
			}

		}

		return t/(t+f);

	}
	
	public static Bitmap getBitmapFromPath(String path) {
		Bitmap bitmap=null;
		bitmap=BitmapFactory.decodeFile(path);
		while(bitmap==null){
			LogUtils.info(ImageUtils.class, "bitmapFactory decodeFile null");
			SleepUtils.sleep(100L);
			bitmap=BitmapFactory.decodeFile(path);
		}
		return bitmap;
	}

}
