package com.albumselector.album.contract;

import com.albumselector.album.ui.mvp.BasePresenter;
import com.albumselector.album.ui.mvp.BaseView;

/**
 * @desc:
 * @author: Leo
 * @date: 2016/11/24
 */
public class PhotoPickerContract {

    public interface View extends BaseView {
    }

    public abstract static class Presenter extends BasePresenter<View> {
    }

    public interface Model{
    }
}