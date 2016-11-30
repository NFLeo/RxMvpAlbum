package com.albumselector.album.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.albumselector.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/25 下午4:40
 * 描述:
 */
public class GlideImageloader {

    public static void displayImage(Activity activity, final ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height) {
        final String finalPath = getPath(path);
        Glide.with(activity).load(finalPath).asBitmap().placeholder(loadingResId)
                .error(failResId).override(width, height)
                .into(imageView);
    }

    public static void displayImage(ImageView imageView, String path, int width) {
        final String finalPath = getPath(path);

        if (imageView == null)
            return;

        Glide.with(imageView.getContext()).load(finalPath).asBitmap()
                .thumbnail(0.4f)
                .placeholder(R.drawable.albumselector_holder_dark)
                .error(R.drawable.albumselector_holder_light)
                .animate(R.anim.slide_alpha_enter)
                .override(width, width)
                .into(imageView);
    }

    public static void displayImage(ImageView imageView, String path) {
        final String finalPath = getPath(path);

        if (imageView == null)
            return;

        Glide.with(imageView.getContext()).load(finalPath).asBitmap()
                .thumbnail(0.4f)
                .placeholder(R.drawable.albumselector_holder_dark)
                .error(R.drawable.albumselector_holder_light)
                .animate(R.anim.slide_alpha_enter)
                .into(imageView);
    }

    public static void pause(Activity activity) {
        Glide.with(activity).pauseRequests();
    }

    public static void resume(Activity activity) {
        Glide.with(activity).resumeRequestsRecursive();
    }

    protected static String getPath(String path) {
        if (path == null) {
            path = "";
        }

        if (!path.startsWith("http") && !path.startsWith("file")) {
            path = "file://" + path;
        }
        return path;
    }
}