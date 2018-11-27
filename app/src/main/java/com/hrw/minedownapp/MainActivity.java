package com.hrw.minedownapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hrw.downapplibrary.MtDownAppHelper;
import com.hrw.downapplibrary.callback.OnDownProgressListener;
import com.hrw.downapplibrary.util.DownStatus;

public class MainActivity extends AppCompatActivity implements OnDownProgressListener {
    String url = "http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_click_down_app:
                MtDownAppHelper.init()
                        .setNotifyIcon(R.mipmap.ic_launcher)
                        .startDownApp(this, 1, url, 1, "test8","App下载更新中")
                        .setOnProgressListener(this);
//                MtDownAppHelper.init().startDownApp(this, 2, url, 17, "test17");
//                MtDownAppHelper.init().startDownApp(this, 3, url, 18, "test18");
                break;
        }
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
