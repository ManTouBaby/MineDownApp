package com.hrw.minedownapp;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hrw.downapplibrary.MtDownAppHelper;
import com.hrw.downapplibrary.MtDownFileHelper;
import com.hrw.downapplibrary.callback.OnDownFileListener;
import com.hrw.downapplibrary.callback.OnDownProgressListener;
import com.hrw.downapplibrary.util.DownStatus;
import com.hrw.downapplibrary.util.DownType;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDownProgressListener, OnDownFileListener {
    String url = "http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk";
    //    String url = "http://192.168.10.33:51004/appstore/thqw20181122.apk";
    String url1 = "http://cdn12.down.apk.gfan.net.cn/Pfiles/2018/11/20/84260_a92c2511-ae46-4d1f-af95-dd8c8bf5127b.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_click_down_app:
//                MtDownAppHelper.init()
//                        .setNotifyIcon(R.mipmap.ic_launcher)
//                        .startDownApp(this, 1, url, 1)
//                        .setOnProgressListener(this);

                MtDownFileHelper.init(this).getConfig()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .setNotifyTitle("TIM App下载")
                        .setDownType(DownType.DOWN_APP)
                        .setSaveFileName("TIM.apk")
                        .setDownFileListener(this)
                        .addDownLoad(url, 1);

//                MtDownAppHelper.init().startDownApp(this, 2, url, 17, "test17");
//                MtDownAppHelper.init().startDownApp(this, 3, url, 18, "test18");
                break;
            case R.id.bt_click_down_app2:
                MtDownAppHelper.init()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .startDownApp(this, 2, url, 2, "TIM.apk")
                        .setOnProgressListener(this);
//                MtDownFileHelper.init(this).getConfig()
//                        .setNotifyIcon(R.mipmap.ic_launcher)
//                        .setNotifyTitle("爱奇艺 App下载")
//                        .setDownType(DownType.DOWN_APP)
//                        .setSaveFileName("AiQY.apk")
//                        .setDownFileListener(this)
//                        .addDownLoad(url1, 2);
                break;
        }
    }

    @Override
    public void onProgress(String downUrl, DownStatus downStatus, int progress) {

    }

    private void getPermission() {
        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.WAKE_LOCK
                ).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MtDownFileHelper.init(this).onDestroy();
    }

    @Override
    public void onProgress(int progress, int downTag, DownStatus downStatus, String msg) {
        switch (downTag) {
            case 1:
                System.out.println("下载一进度:" + progress + "  是否完成:" + downStatus.getValue());
                break;
            case 2:
                System.out.println("=====下载二进度:" + progress + "  是否完成:" + downStatus.getValue());
                break;
            case 3:
                System.out.println("==========下载三进度:" + progress + "  是否完成:" + downStatus.getValue());
                break;
        }
    }


}
