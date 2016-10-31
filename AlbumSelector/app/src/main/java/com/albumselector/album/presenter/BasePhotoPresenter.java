package com.albumselector.album.presenter;

import com.albumselector.album.model.BasePhotoView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午10:53
 */
public interface BasePhotoPresenter {

    void setImageView(BasePhotoView ImageGridView);
    void getImageList(String bucketId, int pageSize, int currentOffset);
    void getFolderList();
}
