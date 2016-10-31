package com.albumselector.album.rxbus.event;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/25 下午3:45
 */
public class ImageViewPagerChangedEvent {

    private int curIndex;
    private int totalSize;
    private boolean isPreview;

    public ImageViewPagerChangedEvent(int curIndex, int totalSize, boolean isPreview) {
        this.curIndex = curIndex;
        this.totalSize = totalSize;
        this.isPreview = isPreview;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public boolean isPreview() {
        return isPreview;
    }
}
