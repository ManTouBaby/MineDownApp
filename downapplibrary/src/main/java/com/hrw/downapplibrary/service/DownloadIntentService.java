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
import com.hrw.downapplibrary.util.DownStatus;
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
    private int notify_icon = -1;

    public DownloadIntentService() {
        super("InitializeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUrl = intent.getExtras().getString(Constant.DOWN_URL);
        final int downloadId = intent.getExtras().getInt(Constant.DOWN_ID);
        mDownloadFileName = intent.getExtras().getString(Constant.DOWN_FILE_NAME);
        mDownloadNotifyTitle = intent.getExtras().getString(Constant.DOWN_NOTIFY_TITLE);
        mDownloadTag = intent.getExtras().getInt(Constant.DOWN_TAG);
        notify_icon = intent.getExtras().getInt(Constant.DOWN_NOTIFY_ICON, -1);


        Log.d(TAG, "download_url --" + downloadUrl);
        Log.d(TAG, "download_file --" + mDownloadFileName);

        final File file = new File(MtFileUtil.getAppPath(this) + Constant.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        int progress = 0;
        if (file.exists()) {
            range = MtSPHelper.getLong(Constant.DOWN_APP_SP_TAG, downloadUrl);//如果文件存在，则获取当前文件已下载大小
            progress = (int) (range * 100 / file.length());//如果文件已下载大小跟文件大小相同，说明文件已下载完成
            if (progress == 100) {
                sendBroadCast(100, DownStatus.DOWN_DONE, "已下载");
                installApp(file);
            }
        }

        Log.d(TAG, "range = " + range);

        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_download);
        remoteViews.setProgressBar(R.id.down_progress, 100, progress, false);
        remoteViews.setTextViewText(R.id.down_title, "已下载" + progress + "%");
        remoteViews.setImageViewResource(R.id.down_icon, notify_icon != -1 ? notify_icon : R.mipmap.down_notify_icon);

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
                remoteViews.setTextViewText(R.id.down_title, mDownloadNotifyTitle == null ? "正在下载" : mDownloadNotifyTitle);
                mNotifyManager.notify(downloadId, mNotification);
                if (progress == 100) {
                    sendBroadCast(100, DownStatus.DOWN_COMPLETE, "下载成功");
                } else {
                    sendBroadCast(progress, DownStatus.DOWN_ING, "下载中");
                }
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "下载完成");
                mNotifyManager.cancel(downloadId);
                installApp(file);
            }

            @Override
            public void onError(String msg) {
                mNotifyManager.cancel(downloadId);
                sendBroadCast(-1, DownStatus.DOWN_FAILS, msg);
                Log.d(TAG, "下载发生错误:" + msg);
            }
        });
    }

    private void sendBroadCast(int progress, DownStatus downStatus, String downMSG) {
        Intent intent = new Intent();

        intent.putExtra(Constant.DOWN_TAG, mDownloadTag);
        intent.putExtra(Constant.DOWN_PROGRESS, progress);
        intent.putExtra(Constant.DOWN_STATUS, downStatus);
        intent.putExtra(Constant.DOWN_MSG, downMSG);
        intent.setAction("com.hrw.DownProgressBroadcast");
        sendBroadcast(intent);
    }

    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
