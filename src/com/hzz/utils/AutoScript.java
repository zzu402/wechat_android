package com.hzz.utils;

import java.util.Random;

import com.hzz.constant.GlobalConstant;



/**
 * @Author: huangzz
 * @Description: 脚本
 * @Date :2018/5/10
 */
public class AutoScript {

	private static int model=1;
	private static void clickContact() {
        AdbUtils.touch(200, 450);
    }

    private static void clickChatInput() {
        AdbUtils.touch(350, 1800);
    }

    private static void inputChatContent(String text) {
    	for(int i=0;i<text.length();i++){
    		AdbUtils.inputText(String.valueOf(text.charAt(i)));
    		SleepUtils.sleep(50L);
    	}
        
    }

    private static void sendText() {
        AdbUtils.touch(940, 1820);
    }
    private static boolean checkHeaderView(String str){
        AdbUtils.printScreen();
        ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
        String text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
        if(!OcrUtils.checkOcrResult(text, model)){
        	return false;
        }
        int times=0;
        while(!text.contains(str)){
            times++;
            if(times>20){
                return false;
            }
            SleepUtils.sleep(200L);
            AdbUtils.printScreen();
            ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
            text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
            if(!OcrUtils.checkOcrResult(text, model)){
            	return false;
            }
        }
        return true;
    }

    private static boolean checkHeaderContainText(String str){
        AdbUtils.printScreen();
        ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
        String text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
        int times=0;
        while(text.contains(str)){
            times++;
            if(times>20){
                return true;
            }
            SleepUtils.sleep(200L);
            AdbUtils.printScreen();
            ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
            text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
        }
        return false;
    }
    
    
    /*
     * 如果ocr识别出错，运行识别，返回false（百度ocr次数到期）
     */
    public static boolean goHome() {//返回微信主页面
        AdbUtils.printScreen();
        ImageUtils.cron(20, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.HOME_LOCATION);
        String text= OcrUtils.ocr(GlobalConstant.HOME_LOCATION,model);
        if(!OcrUtils.checkOcrResult(text, model)){//判断ocr识别结果
        	return false;
        }
        while (!text.contains("微信")) {//
            AdbUtils.touch(50,130);
            AdbUtils.printScreen();
            ImageUtils.cron(20, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.HOME_LOCATION);
            text= OcrUtils.ocr(GlobalConstant.HOME_LOCATION,model);
            if(!OcrUtils.checkOcrResult(text, model)){
            	return false;
            }
        }
        return true;
    }


    //进入添加好友的界面
    public static boolean enterAddSearchView(){
        AdbUtils.touch(970, 120);//点击左上角"+"
        SleepUtils.sleep(100L);
        AdbUtils.touch(700, 410);//点击"添加朋友"
        if(!checkHeaderView("添加")){
            return false;
        }
        AdbUtils.touch(200, 330);//点击"搜索 微信号/QQ号//手机号"
        return  true;
    }
    //输入微信id进行微信号搜索
    public static void doAddSearchAcion(String wechatId){
        checkHeaderContainText("添加朋友");
        Random random=new Random();
//        AdbUtils.inputText(wechatId);//在输入框输入好友 微信号/手机号/qq
        AdbUtils.inputText(wechatId);
        SleepUtils.sleep(100L);
        //这边随便输入一位数字
        AdbUtils.inputText(String.valueOf(random.nextInt(9)));
        //然后删除
        AdbUtils.del();
        //在搜索栏目上面搜索到对象后 点击
        AdbUtils.touch(400, 250);
    }

    //进入到添加界面
    public static boolean  enterAddView(){
        //从上一个页面进来，这时候可能会卡住，先延时1秒
        SleepUtils.sleep(1000L);//
        return checkHeaderView("详细资料");
    }

    //1 表示未添加，这时候要进行点击添加按钮 2表示已经添加了，这时候点击发送消息，0 表示什么都没找到,3表示已经添加但是点击发送消息失败
    public static int doAddFriendAction(String verifyCode){//这个时候该做添加朋友的操作了
        int y = 0;
        ImageUtils.cron(200, 750 + y, 600, 150, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.ADD_LOCATION);//裁剪部分区域
        String text = OcrUtils.ocr(GlobalConstant.ADD_LOCATION,model);//获得裁剪部分区域的文字
        while (y < 900) {
            if (text.contains("添加")) {//该好友并没有添加到通讯录
                AdbUtils.touch(200, 750 + y+70);//点击添加按钮
                AdbUtils.printScreen();
                ImageUtils.cron(0,0,1080,1920,GlobalConstant.SCREENSHOT_LOCATION,GlobalConstant.LAST_PAGE_LOCATION);
                return 1;
            } else if (text.contains("发消息")) {//该好友已经添加到通讯录，可以直接发消息
                if(!sendMsgAfterAddAction(750+y,verifyCode))
                    return 3;
                return  2;
            } else {//什么都没有找到，继续找
                SleepUtils.sleep(100L);
                y += 100;
                ImageUtils.cron(200, 750 + y, 600, 150, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.ADD_LOCATION);
                text = OcrUtils.ocr(GlobalConstant.ADD_LOCATION,model);
            }
        }
        return 0;
    }

    //点完添加后，发送消息
    public static boolean sendMsgAfterAddFriend(String verifyCode){
        //这个地方容易卡住，休眠2秒
        SleepUtils.sleep(2000L);
        AdbUtils.printScreen();
        //截图之后比较下相似,相似再等待2秒
        if(ImageUtils.isSimilarity(GlobalConstant.SCREENSHOT_LOCATION,GlobalConstant.LAST_PAGE_LOCATION)){
            SleepUtils.sleep(2000L);
            AdbUtils.printScreen();
        }
        ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
        String text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
        int times=0;
        while(!text.contains("详细资料")&&!text.contains("验证申请")){
            SleepUtils.sleep(2000L);
            AdbUtils.printScreen();
            ImageUtils.cron(100, 100, 400, 80, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.VERIFY_LOCATION);
            text=OcrUtils.ocr(GlobalConstant.VERIFY_LOCATION,model);
            if(times>5)
                return false;
            times++;
        }
        if(text.contains("验证申请")){
            AdbUtils.touch(900, 150);
            return  false;
        }

        int y=0;
        ImageUtils.cron(200, 750 + y, 600, 150, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.ADD_LOCATION);//裁剪部分区域
        text = OcrUtils.ocr(GlobalConstant.ADD_LOCATION,model);//获得裁剪部分区域的文字
        while(y<900){
            if(text.contains("发消息")){
                if(!sendMsgAfterAddAction(750+y,verifyCode))
                    return false;
                return true;
            }else{
                SleepUtils.sleep(100L);
                y += 100;
                ImageUtils.cron(200, 750 + y, 600, 150, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.ADD_LOCATION);
                text = OcrUtils.ocr(GlobalConstant.ADD_LOCATION,model);//获得裁剪部分区域的文字
            }
        }
       return false;
    }

    private static boolean sendMsgAfterAddAction(int y,String verifyCode){
        AdbUtils.touch(200, y+100);//点击屏幕上面的发消息
        if(checkHeaderContainText("详细资料"))
            return false;
        clickChatInput();//点击使得可以输入
        inputChatContent(verifyCode);//输入消息
        sendText();//点击发送
        return true;
    }

    private static void clickSearch() {
        AdbUtils.touch(780, 120);
    }
    private static void searchFriend(String wechatId) {
        AdbUtils.inputText(wechatId);
    }

    private static boolean queryFriend(String friend) {
        clickSearch();
        searchFriend(friend);
        AdbUtils.printScreen();
        ImageUtils.cron(40, 200, 400, 200, GlobalConstant.SCREENSHOT_LOCATION, GlobalConstant.CONTACT_LOCATION);
        String text=OcrUtils.ocr(GlobalConstant.CONTACT_LOCATION,model);
        return text.equals("联系人") || text.equals( "最常使用") ||text.contains("查找手机");
    }

    public static boolean searchFriendAndSendMessage(String friend, String text) {
        goHome();
        int times=0;
        while(!queryFriend(friend)){
            AdbUtils.touch(1040, 120);
            times++;
            if(times>5)
                return false;
        }
        clickContact();
        clickChatInput();
        inputChatContent(text);
        sendText();
        return true;
    }

    //1-成功 2-表示进入到添加界失败 3-没有找到添加或发送消息按钮,4-发送失败 5-ocr识别出错了
    public static int autoRun(String wechatId,String verifyCode){
        //1-先返回到微信主页面
        if(!goHome())
        	return 5;
        //2-进入微信添加好友界面
        if(!enterAddSearchView())
        	return 5;
        //3-搜索微信号
        doAddSearchAcion(wechatId);
        //4-进入到添加界面
        if(!enterAddView()){
            return 2;
        }
        //5-添加
        int result=doAddFriendAction(verifyCode);
        if(result==1) {
            if(!sendMsgAfterAddFriend(verifyCode)) {
                if(!searchFriendAndSendMessage(wechatId, verifyCode))
                    return 4;
            }
        }else if(result==3){
            if(!searchFriendAndSendMessage(wechatId, verifyCode))
                return 4;
        }
        else if(result==0)
            return 3;
        return 1;
    }
}
