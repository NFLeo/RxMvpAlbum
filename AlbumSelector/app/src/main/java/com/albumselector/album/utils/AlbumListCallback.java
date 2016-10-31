package com.albumselector.album.utils;

import com.albumselector.album.entity.FolderBean;

import java.util.List;

public interface AlbumListCallback {
    void onSuccessBack(List<FolderBean> folderBeen);

    void onErrorBack();
}