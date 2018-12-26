package com.hrw.downapplibrary.util;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/24 17:45
 * @desc:
 */
public enum DownType {
    DOWN_APP("下载APP", "/downLoad/app/"),
    DOWN_MUSIC("下载音乐", "/downLoad/music"),
    DOWN_MOVIE("下载电影", "/downLoad/movie"),
    DOWN_FILE("下载文件", "/downLoad/files"),
    DOWN_IMAGE("下载图片", "/downLoad/images");

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
