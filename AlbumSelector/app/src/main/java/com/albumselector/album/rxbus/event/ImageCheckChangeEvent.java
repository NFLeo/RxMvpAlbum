package com.albumselector.album.rxbus.event;

import com.albumselector.album.entity.ImageBean;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/24 下午11:47
 */
public class ImageCheckChangeEvent {

    private ImageBean imageBean;

    public ImageCheckChangeEvent(ImageBean ImageBean){
        this.imageBean = ImageBean;
    }

    public ImageBean getImageBean() {
        return this.imageBean;
    }
}
