package com.albumselector.album.ui.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.albumselector.album.baserx.RxBusManager;
import com.albumselector.album.ui.listener.OnOnceClickListener;

/**
 * @desc:         MVP模式
 * @author:       Leo
 * @date:         2016/10/26
 * @param <V>     扩展View
 * @param <P>     扩展Presenter
 */
public abstract class BaseMvpActivity<V extends BaseView, P extends BasePresenter<V>>
        extends FragmentActivity implements BaseView {

    protected P presenter;
    protected Context context;
    protected View rootView;
    public RxBusManager rxBusManager;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        rxBusManager = new RxBusManager();
        presenter = createPresenterInstance();
        if (presenter != null) {
            presenter.attachView((V) this);
        }
        rootView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        setContentView(rootView);
        onViewCreated();
    }

    //初始化Presenter在setContentView之前
    protected abstract P createPresenterInstance();

    protected abstract int getLayoutId();

    /**
     * Invoked after {@link #setContentView(int)}
     */
    protected abstract void onViewCreated();

    @Override
    public Activity visitActivity() {
        return getActivity();
    }

    protected Activity getActivity() {
        return this;
    }

    @Override
    public void showToastMsg(String msg) {
    }

    @Override
    public void showProgressingDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    /**
     * Invoke the method after you have implemented method {@link BaseMvpActivity#onViewClicked(View, int)}
     *
     * @param id id of View
     */
    protected void attachClickListener(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
    }

    private OnOnceClickListener clickListener = new OnOnceClickListener() {
        @Override
        public void onOnceClick(View v) {
            onViewClicked(v, v.getId());
        }
    };

    /**
     * Clicked views' implementation
     *
     * @param view which view has clicked
     * @param id   id of View
     */
    protected void onViewClicked(View view, int id) {

    }

    @Override
    protected void onDestroy() {
        rxBusManager.clear();
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }
}