package com.albumselector.album.presenter;

import android.content.Context;

public abstract class BasePresenter<T, E>
{
    public Context context;
    public E mModel;
    public T mView;

    public void setVM(T v, E m) {
        this.mView = v;
        this.mModel = m;
        this.onStart();
    }

    public void onStart(){
    }
    
    public void onDestroy() {
    }
}