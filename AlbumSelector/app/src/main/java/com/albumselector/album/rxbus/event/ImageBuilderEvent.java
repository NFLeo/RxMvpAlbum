package com.albumselector.album.rxbus.event;

import com.albumselector.album.utils.AlbumBuilder;

import java.util.List;

/**
 * @desc:         传递相册配置
 * @author:       Leo
 * @date:         2016/11/28
 */
public class ImageBuilderEvent {

    private AlbumBuilder imageBuilder;

    public ImageBuilderEvent(AlbumBuilder imageBuilder) {
        this.imageBuilder = imageBuilder;
    }

    public AlbumBuilder getImageBuilder() {
        return imageBuilder;
    }
}
