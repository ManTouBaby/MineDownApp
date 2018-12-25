package com.hrw.downapplibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.DrawableRes;

import com.hrw.downapplibrary.bean.ProgressBO;
import com.hrw.downapplibrary.callback.OnDownFileListener;
import com.hrw.downapplibrary.service.DownLoadService;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.downapplibrary.util.DownType;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/24 16:36
 * @desc:
 */
public class MtDownFileHelper {
    private static DownLoadService.DownBinder downBinder;
    private static MtDownFileHelper mDownHelper;
    private static DownLoadService mService;
    private static OnDownFileListener mDownFileListener;
    private Context mContext;

    private MtDownFileHelper(Context context) {
        Intent intent = new Intent(context, DownLoadService.class);
        boolean success = context.getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
        mContext = context;
//        System.out.println("绑定是否成功:" + success);
    }


    /**
     * 初始化
     *
     * @param context
     * @return
     */
    public static MtDownFileHelper init(Context context) {
        if (mDownHelper == null) {
            synchronized (MtDownFileHelper.class) {
                if (mDownHelper == null) {
                    mDownHelper = new MtDownFileHelper(context);
                }
            }
        }
        return mDownHelper;
    }

    public Config getConfig() {
        return new Config();
    }

    public class Config {
        private int notify_icon = R.mipmap.down_notify_icon;
        private String notifyTitle = "正在下载";
        private DownType downType = DownType.DOWN_FILE;
        private String mSaveFileName;


        /**
         * 设置提示标题
         *
         * @param notifyTitle
         */
        public Config setNotifyTitle(String notifyTitle) {
            this.notifyTitle = notifyTitle;
            return this;
        }

        /**
         * 设置下载类型
         */
        public Config setDownType(DownType downType) {
            this.downType = downType;
            return this;
        }

        /**
         * 设置提示图标
         *
         * @param notify_icon
         */
        public Config setNotifyIcon(@DrawableRes int notify_icon) {
            this.notify_icon = notify_icon;
            return this;
        }

        /**
         * 设置保存文件名称
         *
         * @param saveFileName
         */
        public Config setSaveFileName(String saveFileName) {
            this.mSaveFileName = saveFileName;
            return this;
        }


        public Config addDownLoad(String downUrl, int downId) {
            MtSPHelper.init(mContext, Constant.DOWN_APP_SP_TAG);
            if (mSaveFileName == null)
                mSaveFileName = downUrl.substring(downUrl.lastIndexOf("/"), downUrl.length());
            if (mService != null)
                mService.addDownLoad(downId, downUrl, mSaveFileName, notifyTitle, notify_icon, downType);
            return this;
        }

        public Config setDownFileListener(OnDownFileListener downFileListener) {
            mDownFileListener = downFileListener;
            return this;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateProgress(ProgressBO progressBO) {
        if (mDownFileListener != null) {
            mDownFileListener.onProgress(progressBO.url, progressBO.downStatus, progressBO.progress);
        }
    }

    /**
     * 销毁连接
     */
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mContext.getApplicationContext().unbindService(mConnection);
    }

    static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downBinder = (DownLoadService.DownBinder) service;
            mService = downBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

}
