package com.hrw.minedownapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hrw.downapplibrary.service.DownloadIntentService;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int DOWNLOADAPK_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_click_down_app:
                if (isServiceRunning(DownloadIntentService.class.getName())) {
                    Toast.makeText(MainActivity.this, "正在下载", Toast.LENGTH_SHORT).show();
                    return;
                }
                //String downloadUrl = http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk;
                String downloadUrl = "http://192.168.10.33:51004/appstore/thqw20181122.apk";
                Intent intent = new Intent(MainActivity.this, DownloadIntentService.class);
                Bundle bundle = new Bundle();
                bundle.putString("download_url", downloadUrl);
                bundle.putInt("download_id", DOWNLOADAPK_ID);
                bundle.putString("download_file", "TH20181122.apk");
                intent.putExtras(bundle);
                startService(intent);
                break;
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    private boolean isServiceRunning(String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
