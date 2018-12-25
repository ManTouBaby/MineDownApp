package com.hrw.downapplibrary.util;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/24 17:45
 * @desc:
 */
public enum DownType {
    DOWN_APP("下载APP", "/downLoad/App/"),
    DOWN_MUSIC("下载音乐", "/downLoad/Music"),
    DOWN_MOVIE("下载电影", "/downLoad/Movie"),
    DOWN_FILE("下载文件", "/downLoad/Files");

    private String typeName;
    private String path;

    DownType(String typeName, String path) {
        this.typeName = typeName;
        this.path = path;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getPath() {
        return path;
    }
}
