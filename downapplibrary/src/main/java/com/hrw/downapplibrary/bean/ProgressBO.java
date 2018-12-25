package com.hrw.downapplibrary.bean;

import com.hrw.downapplibrary.util.DownStatus;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/25 9:50
 * @desc:
 */
public class ProgressBO {
    public DownStatus downStatus;
    public String url;
    public int progress;

    public ProgressBO(DownStatus downStatus, String url, int progress) {
        this.downStatus = downStatus;
        this.url = url;
        this.progress = progress;
    }
}
