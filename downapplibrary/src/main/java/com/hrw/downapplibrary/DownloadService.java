package com.hrw.downapplibrary;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.File;

/**
 * @author:ccf008
 * @date:2017/10/24 9:31
 * @desc:
 */

public class DownloadService extends Service {
    private DownIBinder downIBinder = new DownIBinder();
    private DownloadManager downloadManager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return downIBinder;
    }

    class DownIBinder extends Binder {

        public long downloadApk(String apkName, String apkUrl) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
            /**设置用于下载时的网络状态*/
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            /**设置通知栏是否可见*/
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            /**设置下载标题*/
            request.setTitle(apkName);
            /**设置下载内容*/
            request.setDescription(apkName + "下载更新");
            /**设置漫游状态下是否可以下载*/
            request.setAllowedOverRoaming(false);
            /**将文件下载到自己的Download文件夹下,必须是External的 这是DownloadManager的限制*/
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName + ".apk");
            request.setDestinationUri(Uri.fromFile(file));
            /**将下载请求放入队列， return下载任务的ID*/
            long downloadId = downloadManager.enqueue(request);
            return downloadId;
        }

        public DownloadBean getProgress(long downloadId) {
            DownloadBean downloadBean = new DownloadBean();
            //查询进度
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int currentSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int totalSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    double currentPresent = (currentSize * 0.1) / (totalSize * 0.1) * 100;
                    downloadBean.setCurrentSize(currentSize);
                    downloadBean.setTotalSize(totalSize);
                    downloadBean.setCurrentPresent(currentPresent);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return downloadBean;
        }
    }


}
