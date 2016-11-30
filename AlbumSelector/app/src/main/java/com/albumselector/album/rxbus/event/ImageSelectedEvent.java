package com.albumselector.album.rxbus.event;

import java.util.List;

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
