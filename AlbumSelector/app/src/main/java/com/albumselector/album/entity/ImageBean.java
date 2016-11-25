package com.albumselector.album.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
  * @desc:         照片实体类
  * @author:       Leo
  * @date:         2016/10/27
  */
public class ImageBean implements Parcelable
{
    private long imageId;           //照片Id
    private String imageFolderId;   //照片相册Id
    private String imageTitle;      //照片名
    private String imageFolderName; //照片相册名
    private long createDate;        //照片创建时间
    private String imagePath;       //照片地址
    private long imageSize;         //照片大小
    private int width;              //宽
    private int height;             //高
    private double latitude;        //拍摄纬度
    private double longitude;       //拍摄经度
    private String mimeType;        //媒体类型
    private boolean isSelected;     //是否选中

    public ImageBean(String imagePath, boolean isSelected) {
        this.isSelected = isSelected;
        this.imagePath = imagePath;
    }

    public String getImageFolderId() {
        return imageFolderId;
    }

    public void setImageFolderId(String imageFolderId) {
        this.imageFolderId = imageFolderId;
    }

    public String getImageFolderName() {
        return imageFolderName;
    }

    public void setImageFolderName(String imageFolderName) {
        this.imageFolderName = imageFolderName;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getImageSize() {
        return imageSize;
    }

    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public ImageBean() {
    }

    public ImageBean(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.imageId);
        dest.writeString(this.imageFolderId);
        dest.writeString(this.imageTitle);
        dest.writeString(this.imageFolderName);
        dest.writeLong(this.createDate);
        dest.writeString(this.imagePath);
        dest.writeLong(this.imageSize);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.mimeType);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    protected ImageBean(Parcel in) {
        this.imageId = in.readLong();
        this.imageFolderId = in.readString();
        this.imageTitle = in.readString();
        this.imageFolderName = in.readString();
        this.createDate = in.readLong();
        this.imagePath = in.readString();
        this.imageSize = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.mimeType = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
