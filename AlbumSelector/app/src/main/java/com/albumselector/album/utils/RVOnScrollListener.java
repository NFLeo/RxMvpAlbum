package com.albumselector.album.utils;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

/**
 * @desc:         recyclerview滑动监听不加载图片
 * @author:       Leo
 * @date:         2016/11/29
 */
public class RVOnScrollListener extends RecyclerView.OnScrollListener {
    private Activity mActivity;

    public RVOnScrollListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            GlideImageloader.resume(mActivity);
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            GlideImageloader.pause(mActivity);
        }
    }
}
