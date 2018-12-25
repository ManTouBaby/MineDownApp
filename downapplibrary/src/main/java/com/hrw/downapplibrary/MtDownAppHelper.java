package com.hrw.downapplibrary;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;

import com.hrw.downapplibrary.callback.OnDownProgressListener;
import com.hrw.downapplibrary.service.DownloadIntentService;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.downapplibrary.util.DownStatus;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2018/11/23 21:21
 * @desc:
 */
public class MtDownAppHelper {
    static MtDownAppHelper mDownAppHelper;
    static OnDownProgressListener mProgressListener;
    private DownProgressBroadcast progressBroadcast;
    private int notifyIcon;


    private MtDownAppHelper() {
    }

    public static MtDownAppHelper init() {
        if (mDownAppHelper == null) {
            mDownAppHelper = new MtDownAppHelper();
        }
        return mDownAppHelper;
    }

    public MtDownAppHelper setOnProgressListener(OnDownProgressListener progressListener) {
        mProgressListener = progressListener;
        return mDownAppHelper;
    }

    public MtDownAppHelper setNotifyIcon(@DrawableRes int notifyIcon) {
        this.notifyIcon = notifyIcon;
        return mDownAppHelper;
    }

    public MtDownAppHelper startDownApp(Context context, int downTag, String downUrl, int downId) {
        startDownApp(context, downTag, downUrl, downId, downUrl.substring(downUrl.lastIndexOf("/"), downUrl.length()));
        return mDownAppHelper;
    }

    public MtDownAppHelper startDownApp(Context context, int downTag, String downUrl, int downId, String saveFileName) {
        startDownApp(context, downTag, downUrl, downId, saveFileName, null);
        return mDownAppHelper;
    }

    public MtDownAppHelper startDownApp(Context context, int downTag, String downUrl, int downId, String saveFileName, String notifyTitle) {
        MtSPHelper.init(context, Constant.DOWN_APP_SP_TAG);
//        if (isServiceRunning(context, DownloadIntentService.class.getName())) {
//            Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show();
//            return mDownAppHelper;
//        }
        Intent intent = new Intent(context, DownloadIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.DOWN_URL, downUrl);
        bundle.putInt(Constant.DOWN_ID, downId);
        bundle.putString(Constant.DOWN_FILE_NAME, saveFileName);
        bundle.putString(Constant.DOWN_NOTIFY_TITLE, notifyTitle);
        bundle.putInt(Constant.DOWN_TAG, downTag);
        bundle.putInt(Constant.DOWN_NOTIFY_ICON, notifyIcon);
        intent.putExtras(bundle);
        context.startService(intent);
        initBroadCast(context);
        return mDownAppHelper;
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
        progressBroadcast = new DownProgressBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hrw.DownProgressBroadcast");
        context.registerReceiver(progressBroadcast, intentFilter);
    }

    private class DownProgressBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int tag = intent.getIntExtra(Constant.DOWN_TAG, 0);
            int progress = intent.getIntExtra(Constant.DOWN_PROGRESS, 0);
            String msg = intent.getStringExtra(Constant.DOWN_MSG);
            DownStatus downStatus = (DownStatus) intent.getSerializableExtra(Constant.DOWN_STATUS);
            if (downStatus == DownStatus.DOWN_COMPLETE || downStatus == DownStatus.DOWN_DONE) {
                context.unregisterReceiver(progressBroadcast);
            }
            if (mProgressListener != null) {
                mProgressListener.onProgress(progress, tag, downStatus, msg);
            }

        }
    }
}
