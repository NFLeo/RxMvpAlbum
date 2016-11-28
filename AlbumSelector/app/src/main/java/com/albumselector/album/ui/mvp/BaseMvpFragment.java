package com.albumselector.album.ui.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.albumselector.album.baserx.RxBusManager;
import com.albumselector.album.ui.listener.OnOnceClickListener;

/**
 * @desc:         MVP模式
 * @author:       Leo
 * @date:         2016/10/26
 * @param <V>     扩展View
 * @param <P>     扩展Presenter
 */
public abstract class BaseMvpFragment<V extends BaseView, P extends BasePresenter<V>>
        extends Fragment implements BaseView {

    protected P presenter;
    private View contentView;
    private RxBusManager rxBusManager;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxBusManager = new RxBusManager();
        presenter = createPresenterInstance();
        if (presenter != null) {
            presenter.attachView((V) this);
        }
    }

    protected abstract P createPresenterInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    protected abstract int getLayoutId();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        contentView = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Activity visitActivity() {
        return getActivity();
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
     * Invoke the method after you have implemented method {@link BaseMvpFragment#onViewClicked(View, int)}
     *
     * @param id id of view
     */
    protected void attachClickListener(int id) {
        if (contentView != null) {
            View view = contentView.findViewById(id);
            if (view != null) {
                view.setOnClickListener(clickListener);
            }
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
     * @param id   id of view
     */
    protected void onViewClicked(View view, int id) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        rxBusManager.clear();
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }

}
