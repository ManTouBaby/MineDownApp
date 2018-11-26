package com.hrw.downapplibrary.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.hrw.downapplibrary.R;
import com.hrw.downapplibrary.callback.DownloadCallBack;
import com.hrw.downapplibrary.http.RetrofitHttp;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.utilslibrary.file.MtFileUtil;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import java.io.File;


public class DownloadIntentService extends IntentService {

    private static final String TAG = "DownloadIntentService";
    private NotificationManager mNotifyManager;
    private String mDownloadFileName;
    private String mDownloadNotifyTitle;
    private int mDownloadTag;
    private Notification mNotification;

    public DownloadIntentService() {
        super("InitializeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUrl = intent.getExtras().getString("download_url");
        final int downloadId = intent.getExtras().getInt("download_id");
        mDownloadFileName = intent.getExtras().getString("download_file");
        mDownloadNotifyTitle = intent.getExtras().getString("download_notify_title");
        mDownloadTag = intent.getExtras().getInt("download_tag");

        Log.d(TAG, "download_url --" + downloadUrl);
        Log.d(TAG, "download_file --" + mDownloadFileName);

        final File file = new File(MtFileUtil.getAppPath(this) + Constant.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        int progress = 0;
        if (file.exists()) {
            range = MtSPHelper.getLong(Constant.SPNAME, downloadUrl);
            progress = (int) (range * 100 / file.length());
            if (range == file.length()) {
                installApp(file);
                return;
            }
        }

        Log.d(TAG, "range = " + range);

        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.down_notify_layout);
        remoteViews.setProgressBar(R.id.down_progress, 100, progress, false);
        remoteViews.setTextViewText(R.id.down_title, "已下载" + progress + "%");

        mNotification = new NotificationCompat.Builder(this)
                .setContent(remoteViews)
                .setTicker("正在下载")
                .setSmallIcon(R.mipmap.down_notify_icon)
                .build();


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(downloadId, mNotification);
        RetrofitHttp.getInstance().downloadFile(this, range, downloadUrl, mDownloadFileName, new DownloadCallBack() {
            @Override
            public void onProgress(int progress) {
                remoteViews.setProgressBar(R.id.down_progress, 100, progress, false);
                remoteViews.setTextViewText(R.id.tv_down_progress_show, "已下载" + progress + "%");
                remoteViews.setTextViewText(R.id.down_title, mDownloadNotifyTitle == null ? "正在下载" : mDownloadFileName);
                mNotifyManager.notify(downloadId, mNotification);
                sendBroadCast(progress, false);
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "下载完成");
                mNotifyManager.cancel(downloadId);
                sendBroadCast(100, true);
                installApp(file);
            }

            @Override
            public void onError(String msg) {
                mNotifyManager.cancel(downloadId);
                Log.d(TAG, "下载发生错误--" + msg);
            }
        });
    }

    private void sendBroadCast(int progress, boolean downComplete) {
        Intent intent = new Intent();
        intent.putExtra("down_tag", mDownloadTag);
        intent.putExtra("down_progress", progress);
        intent.putExtra("down_complete", downComplete);
        intent.setAction("com.hrw.DownProgressBroadcast");
        sendBroadcast(intent);
    }

    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
