package com.hrw.downapplibrary.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.hrw.downapplibrary.R;
import com.hrw.downapplibrary.bean.ProgressBO;
import com.hrw.downapplibrary.callback.DownloadCallBack;
import com.hrw.downapplibrary.http.RetrofitHelper;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.downapplibrary.util.DownStatus;
import com.hrw.downapplibrary.util.DownType;
import com.hrw.utilslibrary.file.MtFileUtil;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/24 11:34
 * @desc:
 */
public class DownLoadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addDownLoad(final int downId, final String downUrl, final String saveFileName, final String notifyTitle, final int notify_icon, final DownType downType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(MtFileUtil.getAppPath(DownLoadService.this) + downType.getPath(), saveFileName);
                long range = 0;
                int progress = 0;
                if (file.exists()) {
                    range = MtSPHelper.getLong(Constant.DOWN_APP_SP_TAG, downUrl, 0);
                    progress = (int) (range * 100 / file.length());//如果文件已下载大小跟文件大小相同，说明文件已下载完成
                    if (progress == 100) {
                        EventBus.getDefault().post(new ProgressBO(DownStatus.DOWN_COMPLETE, downUrl, 100));
                        installApp(file);
                    }
                }

                final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_download);
                remoteViews.setProgressBar(R.id.down_progress, 100, progress, false);
                remoteViews.setTextViewText(R.id.down_title, "已下载" + progress + "%");
                remoteViews.setImageViewResource(R.id.down_icon, notify_icon);

                final Notification mNotification = new NotificationCompat.Builder(DownLoadService.this)
                        .setContent(remoteViews)
                        .setTicker("正在下载")
                        .setSmallIcon(R.mipmap.down_notify_icon)
                        .build();

                final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(downId, mNotification);

                RetrofitHelper.getRetrofit().addDownLoad(DownLoadService.this, range, downUrl, saveFileName, downType, new DownloadCallBack() {
                    @Override
                    public void onProgress(int progress) {
                        remoteViews.setProgressBar(R.id.down_progress, 100, progress, false);
                        remoteViews.setTextViewText(R.id.tv_down_progress_show, "已下载" + progress + "%");
                        remoteViews.setTextViewText(R.id.down_title, notifyTitle);
                        mNotifyManager.notify(downId, mNotification);
//                if (progress == 100) {
//                    EventBus.getDefault().post(new ProgressBO(DownStatus.DOWN_COMPLETE, downUrl, 100));
//                } else {
//                    EventBus.getDefault().post(new ProgressBO(DownStatus.DOWN_ING, downUrl, progress));
//                }
                    }

                    @Override
                    public void onCompleted() {
                        mNotifyManager.cancel(downId);
                    }

                    @Override
                    public void onError(String msg) {
                        mNotifyManager.cancel(downId);
                        EventBus.getDefault().post(new ProgressBO(DownStatus.DOWN_FAILS, downUrl, -1));
                    }
                });

            }
        }).start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownBinder();
    }

    public class DownBinder extends Binder {
        public DownLoadService getService() {
            return DownLoadService.this;
        }
    }

    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
