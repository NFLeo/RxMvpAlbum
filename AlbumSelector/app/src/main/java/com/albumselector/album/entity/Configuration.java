package com.albumselector.album.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Desction:配置信息
 * Author:pengjianbo
 * Date:16/5/7 下午3:58
 */
public class Configuration implements Parcelable {

    private boolean image = true;
    private Context context;
    private List<ImageBean> selectedList;
    private boolean radio;
    private boolean crop;
    private int maxSize = 1;

    private int imageConfig;
    private boolean hideCamera;

    public boolean isImage() {
        return image;
    }

    protected void setImage(boolean image) {
        this.image = image;
    }

    public Context getContext() {
        return context;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    public List<ImageBean> getSelectedList() {
        return selectedList;
    }

    protected void setSelectedList(List<ImageBean> selectedList) {
        this.selectedList = selectedList;
    }

    public boolean isRadio() {
        return radio;
    }

    protected void setRadio(boolean radio) {
        this.radio = radio;
    }

    public int getMaxSize() {
        return maxSize;
    }

    protected void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isHideCamera() {
        return hideCamera;
    }

    public void setHideCamera(boolean hideCamera) {
        this.hideCamera = hideCamera;
    }

    public Bitmap.Config getImageConfig() {
        switch (imageConfig){
            case 1:
                return Bitmap.Config.ALPHA_8;
            case 2:
                return Bitmap.Config.ARGB_4444;
            case 3:
                return Bitmap.Config.ARGB_8888;
            case 4:
                return Bitmap.Config.RGB_565;
        }
        return Bitmap.Config.ARGB_8888;
    }

    public void setImageConfig(int imageConfig) {
        this.imageConfig = imageConfig;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(image ? (byte) 1 : (byte) 0);
        dest.writeTypedList(selectedList);
        dest.writeByte(radio ? (byte) 1 : (byte) 0);
        dest.writeByte(crop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSize);
        dest.writeInt(this.imageConfig);
        dest.writeByte(hideCamera ? (byte) 1 : (byte) 0);
    }

    public Configuration() {
    }

    protected Configuration(Parcel in) {
        this.image = in.readByte() != 0;
        this.selectedList = in.createTypedArrayList(ImageBean.CREATOR);
        this.radio = in.readByte() != 0;
        this.crop = in.readByte() != 0;
        this.maxSize = in.readInt();
        this.imageConfig = in.readInt();
        this.hideCamera = in.readByte() != 0;
    }

    public static final Creator<Configuration> CREATOR = new Creator<Configuration>() {
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}
