package com.hrw.downapplibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hrw.datedialoglib.CommonDialog;
import com.hrw.datedialoglib.DialogType;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @author:ccf008
 * @date:2017/10/23 16:40
 * @desc:
 */

public class DownloadHelper {
    private String TAG = getClass().getName();
    static DownloadHelper managerHelper;
    Context context;
    static String mApkUrl;        //更新地址
    static String mApkName;       //APK名
    static int mApkVersionCode;   //更新版本号码

    private String apkSize;          //更新大小
    private String updateContent;    //更新内容
    private String updateTime;       //更新时间
    private String apkVersionName;   //更新版本名
    private boolean isShowProgress = true;
    private int titleColor = -1;
    DownloadType downloadType = DownloadType.NORMAL_UPDATE;      //更新类型
    DownloadService.DownIBinder downIBinder;
    CommonDialog commonDialog;

    TextView tvProgress;
    TextView tvPresent;
    ProgressBar progressBar;
    private TextView tvAppName;

    public DownloadHelper setApkSize(String apkSize) {
        this.apkSize = apkSize;
        return managerHelper;
    }

    public DownloadHelper setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
        return managerHelper;
    }

    public DownloadHelper setTitleColor(@ColorInt int titleColor) {
        this.titleColor = titleColor;
        return managerHelper;
    }

    public DownloadHelper setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return managerHelper;
    }

    public DownloadHelper setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
        return managerHelper;
    }

    public DownloadHelper setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
        return managerHelper;
    }

    public DownloadHelper setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
        return managerHelper;
    }

    private DownloadHelper(Context context) {
        this.context = context;
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downIBinder = (DownloadService.DownIBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                downIBinder = null;
            }
        }, BIND_AUTO_CREATE);
    }

    public static DownloadHelper instance(@NonNull Context context, @NonNull int apkVersionCode, @NonNull String apkName, @NonNull String apkUrl) {
        if (managerHelper == null) {
            managerHelper = new DownloadHelper(context);
        }
        mApkVersionCode = apkVersionCode;
        mApkName = apkName;
        mApkUrl = apkUrl;
        return managerHelper;
    }

    public void start() {
        if (isShowProgress) {
            startDialogDown();
        } else {
            Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                    .filter(new Func1<Long, Boolean>() {
                        @Override
                        public Boolean call(Long aLong) {
                            return downIBinder != null;
                        }
                    })
                    .map(new Func1<Long, Boolean>() {
                        @Override
                        public Boolean call(Long aLong) {
                            return downIBinder.getProgress(downIBinder.downloadApk(mApkName, mApkUrl)) != null;
                        }
                    })
                    .takeUntil(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean aBoolean) {
                            return aBoolean;
                        }
                    })
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            System.out.println("onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("onError");
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            System.out.println("onNext");
                        }
                    });

        }
    }

    /**
     * 开启带窗口下载
     */
    private void startDialogDown() {
        final CommonDialog commonDialog;
        int systemVersion = getVersionCode(context);
        if (mApkVersionCode > systemVersion) {
            if (downloadType == DownloadType.NORMAL_UPDATE) {
                commonDialog = new CommonDialog(context, DialogType.TOUCH_OUT_SIDE_CANCELED, R.layout.item_popuwindow_update_layout);
            } else {
                commonDialog = new CommonDialog(context, DialogType.TOUCH_OUT_SIDE_NO_CANCELED, R.layout.item_popuwindow_update_layout);
                commonDialog.getText(R.id.tv_item_update_cancel).setVisibility(View.GONE);
            }

            if (apkSize == null)
                commonDialog.getLinearLayout(R.id.ll_item_update_appSize).setVisibility(View.GONE);
            if (updateContent == null)
                commonDialog.getLinearLayout(R.id.ll_item_update_updateContent).setVisibility(View.GONE);
            if (updateTime == null)
                commonDialog.getLinearLayout(R.id.ll_item_update_updateTime).setVisibility(View.GONE);
            if (apkVersionName == null)
                commonDialog.getLinearLayout(R.id.ll_item_update_versionCode).setVisibility(View.GONE);

            commonDialog.text(R.id.tv_item_update_appSize, apkSize);
            commonDialog.text(R.id.tv_item_update_appName, mApkName);
            if (titleColor != -1) commonDialog.textColor(R.id.tv_item_update_appName, titleColor);
            commonDialog.text(R.id.tv_item_update_updateTime, updateTime);
            commonDialog.text(R.id.tv_item_update_versionCode, apkVersionName);
            commonDialog.text(R.id.tv_item_update_updateContent, updateContent);

            commonDialog.getText(R.id.tv_item_update_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dismiss();
                }
            });
            if (titleColor != -1) commonDialog.textColor(R.id.tv_item_update_commit, titleColor);
            commonDialog.getText(R.id.tv_item_update_commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    startCheckProgress(downIBinder.downloadApk(mApkName, mApkUrl));
                    commonDialog.dismiss();
                }
            });
            commonDialog.show();
        }
    }

    /**
     * 开启进度窗口
     *
     * @param downloadId
     */
    private void startCheckProgress(final long downloadId) {
        Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return downIBinder != null;
                    }
                })
                .map(new Func1<Long, DownloadBean>() {
                    @Override
                    public DownloadBean call(Long aLong) {
                        DownloadBean downloadBean = downIBinder.getProgress(downloadId);
                        return downloadBean;
                    }
                })
                .takeUntil(new Func1<DownloadBean, Boolean>() {
                    @Override
                    public Boolean call(DownloadBean downloadBean) {
                        return (downloadBean.getCurrentSize() * 0.1) / (downloadBean.getTotalSize() * 0.1) * 100 == 100;
                    }
                })
                .distinct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadBean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "App Download onCompleted");
                        if (commonDialog != null) commonDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "App Download onError:" + e.toString());
                        if (commonDialog != null) commonDialog.dismiss();
                    }

                    @Override
                    public void onNext(final DownloadBean downloadBean) {
                        int currentSize = downloadBean.getCurrentSize();
                        int totalSize = downloadBean.getTotalSize();
                        double currentPresent = downloadBean.getCurrentPresent();

                        if (tvProgress != null)
                            tvProgress.setText(byteChange(currentSize) + "/" + byteChange(totalSize));
                        if (tvPresent != null) tvPresent.setText(keep2Point(currentPresent) + "%");
                        if (progressBar != null) progressBar.setProgress((int) currentPresent);

                    }
                });
    }

    /**
     * 展示进度窗口
     */
    private void showProgress() {
        if (downloadType == DownloadType.NORMAL_UPDATE) {
            commonDialog = new CommonDialog(context, DialogType.TOUCH_OUT_SIDE_CANCELED, R.layout.item_popuwindow_update_progress_layout);
        } else {
            commonDialog = new CommonDialog(context, DialogType.TOUCH_OUT_SIDE_NO_CANCELED, R.layout.item_popuwindow_update_progress_layout);
            commonDialog.getText(R.id.tv_item_update_cancel).setVisibility(View.GONE);
        }

        tvProgress = commonDialog.getText(R.id.tv_item_popuWindow_update_progress_size);
        tvPresent = commonDialog.getText(R.id.tv_item_popuWindow_update_progress_present);
        if (titleColor != -1)
            commonDialog.textColor(R.id.tv_item_popuWindow_update_progress_appName, titleColor);
        tvAppName = commonDialog.getText(R.id.tv_item_popuWindow_update_progress_appName);
        progressBar = commonDialog.getProgressBar(R.id.pb_item_popuWindow_update_progressbar);
        commonDialog.show();
    }

    /**
     * 字节转换成单位
     *
     * @param size
     * @return
     */
    private String byteChange(long size) {
        String result;
        double kSum = size / 1024.0;
        double mSum = kSum / 1024.0;
        double gSum = mSum / 1024.0;
        if (size < 1024) {
            if (size < 0) size = 0;
            result = String.valueOf((int) size) + " B";
        } else if (kSum > 1 && mSum < 1) {
            result = String.valueOf((int) kSum) + " K";
        } else if (mSum > 1 && gSum < 1) {
            result = keep2Point(mSum) + " M";
        } else {
            result = keep2Point(gSum) + " G";
        }
        return result;
    }

    /**
     * 保留两位小数
     *
     * @param sum
     * @return
     */
    protected static String keep2Point(double sum) {
        int intSum = (int) (sum * 100);
        String result = String.valueOf((double) intSum / 100);
        return result;
    }

    public enum DownloadType {
        NORMAL_UPDATE,
        FORCE_UPDATE
    }

    /**
     * 方法: getVersionCode
     * 描述: 获取客户端版本号
     *
     * @return int    版本号
     */
    private static int getVersionCode(Context context) {
        int versionCode;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            versionCode = 999;
        }
        return versionCode;
    }
}
