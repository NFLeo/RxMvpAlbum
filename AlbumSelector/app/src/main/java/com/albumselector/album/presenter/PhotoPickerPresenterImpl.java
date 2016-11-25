package com.albumselector.album.presenter;
import android.content.Context;

import com.albumselector.album.contract.PhotoPickerContract;

/**
 * @desc:         图片展示Presenter
 * @author:       Leo
 * @date:         2016/11/24
 */
public class PhotoPickerPresenterImpl extends PhotoPickerContract.Presenter
{
    private Context context;

    public PhotoPickerPresenterImpl(Context context) {
        this.context = context;
    }
}