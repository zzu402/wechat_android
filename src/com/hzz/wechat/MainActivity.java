package com.hzz.wechat;


import com.hzz.constant.GlobalConstant;
import com.hzz.utils.ImageUtils;
import com.hzz.utils.RootShellCmd;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity=this;
        ImageUtils.initLoader(this);
        View startBtn = findViewById(R.id.start_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AutoService.class);
                CheckBox checkBox = (CheckBox) findViewById(R.id.check_float);
                if (checkBox.isChecked())
                    intent.putExtra(AutoService.ACTION, AutoService.SHOW);
                startService(intent);
                moveTaskToBack(true);
            }
        });

        findViewById(R.id.end_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AutoService.class);
                stopService(intent);
            }
        });
        
        RootShellCmd cmd = new RootShellCmd();
        TextView info3 = (TextView) findViewById(R.id.info3_text);
 
        // check screenshot
        try {
            cmd.screenCapture(GlobalConstant.SCREENSHOT_LOCATION);
            SystemClock.sleep(1500);

//            Bitmap test = cmd.getBitmapFromPath(GlobalConstant.SCREENSHOT_LOCATION);
            Bitmap test=ImageUtils.getBitmapFromPath(GlobalConstant.SCREENSHOT_LOCATION);
            if (test == null || test.getWidth() == 0 || test.getHeight() == 0) {
                String info = "Not Support the device. Wait for a new version.";
                Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
                info3.setText(info);
                info3.setVisibility(View.VISIBLE);
                startBtn.setEnabled(false);
            }
        } catch (Exception e) {
            String info = "Not Support the device. Wait for a new version.";
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
            info3.setText(info);
            info3.setVisibility(View.VISIBLE);
            startBtn.setEnabled(false);
        }
        // check root
        try {
            cmd.checkRoot();
        } catch (Exception e) {
            String info = "Need to Root";
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
            info3.setText(info);
            info3.setVisibility(View.VISIBLE);
            startBtn.setEnabled(false);
        }    
    }

}
