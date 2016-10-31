package com.albumselector.album.rxbus.event;

import com.albumselector.album.entity.ImageBean;

import java.util.List;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/8/1 下午10:52
 */
public class ImageMultipleResultEvent implements BaseResultEvent {
    private List<ImageBean> ImageResultList;

    public ImageMultipleResultEvent(List<ImageBean> list) {
        this.ImageResultList = list;
    }

    public List<ImageBean> getResult(){
        return ImageResultList;
    }
}
