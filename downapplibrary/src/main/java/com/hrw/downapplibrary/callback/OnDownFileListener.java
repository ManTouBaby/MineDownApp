package com.hrw.downapplibrary.callback;

import com.hrw.downapplibrary.util.DownStatus;

/**
 * @author:MtBaby
 * @date:2018/11/26 21:37
 * @desc:
 */
public interface OnDownFileListener {
    void onProgress(String downUrl, DownStatus downStatus, int progress);
}
