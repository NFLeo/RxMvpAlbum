package com.albumselector.album.utils;

/**
 * @desc:         Builder模式
 * @author:       Leo
 * @date:         2016/11/28
 */
public class AlbumBuilder implements Cloneable
{
    private boolean isTakeCamera;           //是否拍照
    private boolean isMutiSelect;           //是否多选
    private boolean isCrop;                 //是否裁剪
    private int maxSize;                    //最多可选

    public AlbumBuilder(Builder builder) {
        this.isTakeCamera = builder.isTakeCamera;
        this.isTakeCamera = builder.isTakeCamera;
        this.isMutiSelect = builder.isMutiSelect;
        this.isCrop = builder.isCrop;
        this.maxSize = builder.maxSize;
    }

    public static class Builder
    {
        private boolean isTakeCamera;           //是否拍照
        private boolean isMutiSelect;           //是否多选
        private boolean isCrop;                 //是否裁剪
        private int maxSize = 1;                    //最多可选

        public Builder setTakeCamera(boolean takeCamera) {
            isTakeCamera = takeCamera;
            return this;
        }

        public Builder setMutiSelect(boolean mutiSelect) {
            isMutiSelect = mutiSelect;
            if (!isMutiSelect)
                maxSize = 1;
            return this;
        }

        public Builder setCrop(boolean crop) {
            isCrop = crop;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            if (maxSize > 1)
                isMutiSelect = true;
            return this;
        }

        public AlbumBuilder build() {
            return new AlbumBuilder(this);
        }
    }

    public boolean isTakeCamera() {
        return isTakeCamera;
    }

    public void setTakeCamera(boolean takeCamera) {
        isTakeCamera = takeCamera;
    }

    public boolean isMutiSelect() {
        return isMutiSelect;
    }

    public void setMutiSelect(boolean mutiSelect) {
        isMutiSelect = mutiSelect;
    }

    public boolean isCrop() {
        return isCrop;
    }

    public void setCrop(boolean crop) {
        isCrop = crop;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected AlbumBuilder clone() {
        AlbumBuilder albumBuilder = null;

        try {
            albumBuilder = (AlbumBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return albumBuilder;
    }
}
