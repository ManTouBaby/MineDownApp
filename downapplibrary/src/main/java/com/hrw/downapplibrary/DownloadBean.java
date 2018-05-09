package com.hrw.downapplibrary;

/**
 * @author:ccf008
 * @date:2017/10/26 15:22
 * @desc:
 */

public class DownloadBean {
    private int currentSize;
    private int totalSize;
    private double currentPresent;

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public double getCurrentPresent() {
        return currentPresent;
    }

    public void setCurrentPresent(double currentPresent) {
        this.currentPresent = currentPresent;
    }
}
