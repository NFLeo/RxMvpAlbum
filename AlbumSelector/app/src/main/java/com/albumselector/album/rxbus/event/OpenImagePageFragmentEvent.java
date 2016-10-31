package com.albumselector.album.rxbus.event;

import com.albumselector.album.entity.ImageBean;

import java.util.ArrayList;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/27 下午11:14
 */
public class OpenImagePageFragmentEvent {
    private ArrayList<ImageBean> ImageBeanList;
    private int position;

    public OpenImagePageFragmentEvent(ArrayList<ImageBean> ImageBeanList, int position){
        this.ImageBeanList = ImageBeanList;
        this.position = position;
    }

    public ArrayList<ImageBean> getImageBeanList() {
        return ImageBeanList;
    }

    public int getPosition() {
        return position;
    }
}
