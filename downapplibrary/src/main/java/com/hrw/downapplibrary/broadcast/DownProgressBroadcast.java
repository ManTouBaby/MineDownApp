package com.hrw.downapplibrary.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author:MtBaby
 * @date:2018/11/26 23:03
 * @desc:
 */
public class DownProgressBroadcast extends BroadcastReceiver {
    public DownProgressBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int tag = intent.getIntExtra("down_tag", 0);
        int progress = intent.getIntExtra("down_progress", 0);
        boolean isCompleted = intent.getBooleanExtra("down_complete", false);
//        switch (tag) {
//            case 1:
//                System.out.println("下载一进度:" + progress + "  是否完成:" + isCompleted);
//                break;
//            case 2:
//                System.out.println("=====下载二进度:" + progress + "  是否完成:" + isCompleted);
//                break;
//            case 3:
//                System.out.println("==========下载三进度:" + progress + "  是否完成:" + isCompleted);
//                break;
//        }
//        if (mProgressListener != null) {
//            mProgressListener.onProgress(progress, tag, isComplete);
//        }

    }
}
