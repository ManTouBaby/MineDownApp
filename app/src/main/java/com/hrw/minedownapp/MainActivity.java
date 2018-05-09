package com.hrw.minedownapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hrw.downapplibrary.DownloadHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_click_down_app:
                DownloadHelper.instance(this, 2, "DownTest", "http://apk.gfan.net.cn/index.php?c=api&m=down&src=wap&apk=21000k")
                        .setShowProgress(true)
                        .start();
                break;
        }
    }
}
