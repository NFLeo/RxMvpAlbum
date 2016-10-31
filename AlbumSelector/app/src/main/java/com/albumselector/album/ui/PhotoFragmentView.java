package com.albumselector.album.ui;

import java.util.ArrayList;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 下午9:56
 */
public interface PhotoFragmentView<T> {

    void showImageGridFragment();
    void showImagePageFragment(ArrayList<T> list, int position);
    void showImagePreviewFragment();
}
