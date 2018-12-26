package com.hrw.minedownapp;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
    String url2 = "https://d11.baidupcs.com/file/abe4a5efaee8324e44cadb51864ec46d?bkt=p3-0000506aad7e82a4047d641358cb045051ca&xcode=29ed703f078b6a8a00616a655fc6e2958392e4698275b882e47a9881f48c155423ccb069314395085216272c49ae57bb316128a2cdfcce4d&fid=2670346535-250528-276629753478768&time=1545810706&sign=FDTAXGERLQBHSKf-DCb740ccc5511e5e8fedcff06b081203-SPoJ0%2BF%2BafAKerVwhClPMhSdT5M%3D&to=d11&size=14058192&sta_dx=14058192&sta_cs=74074&sta_ft=mp3&sta_ct=5&sta_mt=5&fm2=MH%2CQingdao%2CAnywhere%2C%2Cguangdong%2Cct&ctime=1542938315&mtime=1542938315&resv0=cdnback&resv1=0&vuk=2670346535&iv=0&htype=&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=0000506aad7e82a4047d641358cb045051ca&sl=76480590&expires=8h&rt=sh&r=256260611&mlogid=8341552859119258139&vbdid=1918728818&fin=%E5%B1%95%E5%B1%95%E4%B8%8E%E7%BD%97%E7%BD%97+-+%E6%B2%99%E6%BC%A0%E9%AA%86%E9%A9%BC.mp3&fn=%E5%B1%95%E5%B1%95%E4%B8%8E%E7%BD%97%E7%BD%97+-+%E6%B2%99%E6%BC%A0%E9%AA%86%E9%A9%BC.mp3&rtype=1&dp-logid=8341552859119258139&dp-callid=0.1.1&hps=1&tsl=80&csl=80&csign=chLQ%2BLYCQVOEYzDjMXSMpjfeF8k%3D&so=0&ut=6&uter=4&serv=0&uc=4192912830&ti=26fa64dbec28822470c6c07e1bcacd16bb404c9a5a149025305a5e1275657320&by=themis";
    String url3 = "http://www.xz7.com/up/2017-1/2017149321.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
        MtDownFileHelper.init(this).onCreate();
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

                break;
            case R.id.bt_click_down_app2:
//                MtDownAppHelper.init()
//                        .setNotifyIcon(R.mipmap.ic_launcher)
//                        .startDownApp(this, 2, url, 2, "TIM.apk")
//                        .setOnProgressListener(this);
                MtDownFileHelper.init(this).getConfig()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .setNotifyTitle("爱奇艺 App下载")
                        .setDownType(DownType.DOWN_APP)
                        .setSaveFileName("AiQY.apk")
                        .setDownFileListener(this)
                        .addDownLoad(url1, 2);
                break;
            case R.id.bt_click_down_app3:
                MtDownFileHelper.init(this).getConfig()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .setNotifyTitle("沙漠骆驼.mp3")
                        .setDownType(DownType.DOWN_MUSIC)
                        .setSaveFileName("沙漠骆驼.mp3")
                        .setDownFileListener(this)
                        .addDownLoad(url2, 3);
                break;
            case R.id.bt_click_down_app4:
                MtDownFileHelper.init(this).getConfig()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .setNotifyTitle("图片下载")
                        .setDownType(DownType.DOWN_IMAGE)
                        .setDownFileListener(this)
                        .addDownLoad(url3, 4);
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
        System.out.println("mainActivity onDestroy");
        MtDownFileHelper.init(this).onDestroy();
        super.onDestroy();
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
