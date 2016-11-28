package com.albumselector.album.rxbus.event;

import com.albumselector.album.entity.ImageBean;

import java.util.List;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/24 下午11:47
 */

 /**
  * @desc:         传递选中图片集合
  * @author:       Leo
  * @date:         2016/11/25
  */
public class ImageSelectedEvent {

    private List<String> imageBeanList;

    public ImageSelectedEvent(List<String> imageBeanList){
        this.imageBeanList = imageBeanList;
    }

    public List<String> getImageBean() {
        return this.imageBeanList;
    }
}
