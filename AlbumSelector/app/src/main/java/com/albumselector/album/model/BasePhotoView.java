package com.albumselector.album.model;

import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.entity.ImageBean;

import java.util.List;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午11:00
 */
public interface BasePhotoView
{
    void imageCallback(List<ImageBean> list);
    void folderCallback(List<FolderBean> list);
    void errorCallback();
}
