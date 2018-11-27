package com.hrw.minedownapp;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hrw.downapplibrary.MtDownAppHelper;
import com.hrw.downapplibrary.callback.OnDownProgressListener;
import com.hrw.downapplibrary.util.DownStatus;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDownProgressListener {
    //    String url = "http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk";
    String url = "http://192.168.10.33:51004/appstore/thqw20181122.apk";
//    String url = "http://cdn12.down.apk.gfan.net.cn/Pfiles/2017/03/24/149942_82841bf6-341e-4e03-b90b-547f908871c1.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_click_down_app:
                MtDownAppHelper.init()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .startDownApp(this, 1, url, 1, "test14", "App下载更新中")
                        .setOnProgressListener(this);
//                MtDownAppHelper.init().startDownApp(this, 2, url, 17, "test17");
//                MtDownAppHelper.init().startDownApp(this, 3, url, 18, "test18");
                break;
        }
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
