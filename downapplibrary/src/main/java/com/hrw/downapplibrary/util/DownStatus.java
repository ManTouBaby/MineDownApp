package com.hrw.downapplibrary.util;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/11/27 10:03
 * @desc:
 */
public enum DownStatus {
    DOWN_COMPLETE("下载完成"),
    DOWN_FAILS("下载失败"),
    DOWN_ING("下载中"),
    DOWN_DONE("已下载");

    String mValue;

    DownStatus(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }
}
