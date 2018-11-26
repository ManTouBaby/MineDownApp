package com.hrw.downapplibrary;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.hrw.downapplibrary.callback.OnDownProgressListener;
import com.hrw.downapplibrary.service.DownloadIntentService;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2018/11/23 21:21
 * @desc:
 */
public class MtDownAppHelper {
    static OnDownProgressListener mProgressListener;
    static MtDownAppHelper mDownAppHelper;

    private MtDownAppHelper() {
    }

    public static MtDownAppHelper init() {
        if (mDownAppHelper == null) {
            mDownAppHelper = new MtDownAppHelper();
        }
        return mDownAppHelper;
    }

    public void setOnProgressListener(OnDownProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public void startDownApp(Context context, int downTag, String downUrl, int downId) {
        startDownApp(context, downTag, downUrl, downId, downUrl.substring(downUrl.lastIndexOf("/"), downUrl.length() - 1));
    }

    public void startDownApp(Context context, int downTag, String downUrl, int downId, String saveFileName) {
        startDownApp(context, downTag, downUrl, downId, saveFileName, null);
    }

    public void startDownApp(Context context, int downTag, String downUrl, int downId, String saveFileName, String notifyTitle) {
        MtSPHelper.init(context, Constant.SPNAME);
//        if (isServiceRunning(context, DownloadIntentService.class.getName())) {
//            Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Intent intent = new Intent(context, DownloadIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("download_url", downUrl);
        bundle.putInt("download_id", downId);
        bundle.putString("download_file", saveFileName);
        bundle.putString("download_notify_title", notifyTitle);
        bundle.putInt("download_tag", downTag);
        intent.putExtras(bundle);
        context.startService(intent);
        initBroadCast(context);
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    private boolean isServiceRunning(Context context, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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

    private void initBroadCast(Context context) {
        DownProgressBroadcast progressBroadcast = new DownProgressBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hrw.DownProgressBroadcast");
        context.registerReceiver(progressBroadcast, intentFilter);
    }

    public class DownProgressBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("com.hrw.DownProgressBroadcast")) {
//
//            }
            int tag = intent.getIntExtra("down_tag", 0);
            int progress = intent.getIntExtra("down_progress", 0);
            boolean isComplete = intent.getBooleanExtra("down_complete", false);
            if (mProgressListener != null) {
                mProgressListener.onProgress(progress, tag, isComplete);
            }
        }
    }
}
