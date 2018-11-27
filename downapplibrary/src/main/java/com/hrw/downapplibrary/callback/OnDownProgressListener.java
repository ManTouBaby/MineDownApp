package com.hrw.downapplibrary.callback;

import com.hrw.downapplibrary.util.DownStatus;

/**
 * @author:MtBaby
 * @date:2018/11/26 21:37
 * @desc:
 */
public interface OnDownProgressListener {
    void onProgress(int progress, int downTag, DownStatus downStatus,String msg);
}
