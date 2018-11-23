package com.hrw.downapplibrary.util;

import android.os.Environment;

import com.damon.download.MainApplication;


public class Constant {
    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MainApplication.getInstance().getPackageName();
    public final static String DOWNLOAD_DIR = "/downlaod/";
}
