package com.hzz.constant;
import android.annotation.SuppressLint;
import java.io.File;
/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/27
 */
public class GlobalConstant {

    @SuppressLint("SdCardPath") 
    public static String IMAGE_LOCATION= "mnt/sdcard/wechat";//图片目录

    public static String SCREENSHOT_LOCATION = IMAGE_LOCATION+"/screenshot.png";//截屏位置

    public static String HOME_LOCATION=IMAGE_LOCATION+"/home.png";

    public static String ADD_LOCATION=IMAGE_LOCATION+"/add.png";

    public static String MESSAGE_LOCATION=IMAGE_LOCATION+"/message.png";

    public static String INPUT_LOCATION=IMAGE_LOCATION+"/input.png";

    public static String LAST_PAGE_LOCATION=IMAGE_LOCATION+"/last_page.png";


    public static String CONTACT_LOCATION=IMAGE_LOCATION+"/contact.png";

    public static String VERIFY_LOCATION=IMAGE_LOCATION+"/verify.png";
    static {
        File file=new File(IMAGE_LOCATION);
        if(!file.exists())
            file.mkdirs();

    }



}
