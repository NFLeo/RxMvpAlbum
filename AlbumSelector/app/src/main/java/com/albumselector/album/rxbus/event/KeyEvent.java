package com.albumselector.album.rxbus.event;


/**
 * @desc: 用于RxBus传递标志量
 * @author: Leo
 * @date: 2016/11/15
 */
public class KeyEvent {
    private Object value;
    private String key;

    //相册位置记录
    public static final String PHOTO_FOLDER = "PHOTO_FOLDER";

    //拍照后照片
    public static final String PHOTO_CAMERA = "PHOTO_CAMERA";

    //裁剪后照片
    public static final String PHOTO_CAMERA_CROP = "PHOTO_CAMERA_CROP";

    public KeyEvent(String key, Object value) {
        this.value = value;
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
