package com.hrw.downapplibrary.callback;


public interface DownloadCallBack {

    void onProgress(int progress, long currentSize, long totalSize);

    void onCompleted();

    void onError(String msg);

}
