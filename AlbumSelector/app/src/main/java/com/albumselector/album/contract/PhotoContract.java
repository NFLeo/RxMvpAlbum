package com.albumselector.album.contract;

import com.albumselector.album.model.BasePhotoView;
import com.albumselector.album.presenter.BasePresenter;

/**
 * @desc:
 * @author: Leo
 * @date: 2016/10/27
 */
public class PhotoContract
{
    public interface View extends BasePhotoView {
    }

    public abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void setImageView(BasePhotoView ImageGridView);
        public abstract void getImageList(String bucketId, int pageSize, int currentOffset);
        public abstract void getFolderList();
    }

    public interface Model{
    }


}