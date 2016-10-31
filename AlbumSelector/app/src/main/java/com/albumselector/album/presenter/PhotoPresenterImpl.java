package com.albumselector.album.presenter;
import android.content.Context;

import com.albumselector.album.contract.PhotoContract;
import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.model.BasePhotoView;
import com.albumselector.album.utils.ImageFileManager;

import java.util.List;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by MVPHelper on 2016/10/27
*/

public class PhotoPresenterImpl extends PhotoContract.Presenter {

    private Context context;

    public PhotoPresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void setImageView(BasePhotoView ImageGridView) {

    }

    @Override
    public void getImageList(final String folderId, final int PageIndex, final int PageSize)
    {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<ImageBean>>() {
            @Override
            public void call(Subscriber<? super List<ImageBean>> subscriber) {
                List<ImageBean> imageBean = ImageFileManager.loadAllImage(context, folderId, PageIndex, PageSize);
                subscriber.onNext(imageBean);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.errorCallback();
                    }

                    @Override
                    public void onNext(List<ImageBean> imageBeen) {
                        mView.imageCallback(imageBeen);
                    }
                });
    }

    @Override
    public void getFolderList()
    {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<FolderBean>>() {
            @Override
            public void call(Subscriber<? super List<FolderBean>> subscriber) {
                List<FolderBean> folderBeen = ImageFileManager.loadAllFolder(context);
                subscriber.onNext(folderBeen);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FolderBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.errorCallback();
                    }

                    @Override
                    public void onNext(List<FolderBean> folderBeen) {
                        mView.folderCallback(folderBeen);
                    }
                });
    }
}