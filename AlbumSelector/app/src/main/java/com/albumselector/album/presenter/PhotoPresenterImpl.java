package com.albumselector.album.presenter;

import android.content.Context;
import android.util.Log;

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
        Log.e("TIME", "time X");
//        ImageQueryUtils.getInstance().getImagePhoto(context);
//        Subscription subscriptionImageQueryEvent = RxBus.getDefault().toObservable(ImageQueryResultEvent.class)
//                .map(new Func1<ImageQueryResultEvent, ImageQueryResultEvent>() {
//                    @Override
//                    public ImageQueryResultEvent call(ImageQueryResultEvent openImagePreviewFragmentEvent) {
//                        Log.e("TIME", "time SS");
//                        return openImagePreviewFragmentEvent;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new RxBusSubscriber<ImageQueryResultEvent>() {
//                    @Override
//                    protected void onEvent(ImageQueryResultEvent o) throws Exception {
//                        Log.e("TIME", "time SSS");
//                        mView.imageCallback(o.getResult());
//                    }
//                });
//
//        RxBus.getDefault().add(subscriptionImageQueryEvent);

        rx.Observable.create(new rx.Observable.OnSubscribe<List<ImageBean>>() {
            @Override
            public void call(Subscriber<? super List<ImageBean>> subscriber) {
                List<ImageBean> imageBean = ImageFileManager.loadAllImage(context, folderId, PageIndex, PageSize);
                Log.e("TIME", "time XX");
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
                        Log.e("TIME", "time XXX");
                        mView.imageCallback(imageBeen);
                    }
                });
    }

    @Override
    public void getFolderList()
    {
        Log.e("TIME", "time 0");
        rx.Observable.create(new rx.Observable.OnSubscribe<List<FolderBean>>() {
            @Override
            public void call(Subscriber<? super List<FolderBean>> subscriber) {
                Log.e("TIME", "time 00");
                List<FolderBean> folderBeen = ImageFileManager.loadAllFolders(context);
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
                        Log.e("TIME", "time 000");
                        mView.folderCallback(folderBeen);
                    }
                });
    }
}