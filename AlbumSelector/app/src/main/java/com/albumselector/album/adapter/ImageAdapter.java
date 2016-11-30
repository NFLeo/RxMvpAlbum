package com.albumselector.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albumselector.R;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.rxbus.event.ImageSelectedEvent;
import com.albumselector.album.utils.AlbumBuilder;
import com.albumselector.album.utils.GlideImageloader;
import com.albumselector.album.widget.RecyclerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/18 下午7:48
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.GridViewHolder> {

    private Activity context;
    private List<String> mImageBeanList;
    private LayoutInflater mInflater;
    private AlbumBuilder albumBuilder;
    private int mImageSize;
    private int mCameraTextColor;

    public ImageAdapter(Activity context, List<String> list, int screenWidth, AlbumBuilder albumBuilder) {
        this.context = context;
        this.albumBuilder = albumBuilder;
        this.mImageBeanList = list;
        this.mInflater = LayoutInflater.from(context);
        this.mImageSize = screenWidth/3;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image_list, parent, false);
        return new GridViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {

        if (mImageBeanList == null || mImageBeanList.size() == 0)
            return;

        String imageBean = mImageBeanList.get(position);
        if(position == 0 && albumBuilder.isTakeCamera() && "".equals(imageBean)) {
            holder.mCbCheck.setVisibility(View.GONE);
            holder.mIvImageImage.setVisibility(View.GONE);
            holder.mIvImageImage.setImageBitmap(null);
            holder.mIvCameraImage.setImageResource(R.drawable.selecter_photo_button);
        } else {
            holder.mCbCheck.setVisibility(View.VISIBLE);
            holder.mIvImageImage.setVisibility(View.VISIBLE);
            holder.mCbCheck.setChecked(selectedImageBean.contains(imageBean));
            GlideImageloader.displayImage(holder.mIvImageImage, imageBean, mImageSize);
        }
    }

    @Override
    public int getItemCount() {
        return mImageBeanList.size();
    }

    //记录上次选择，用于单选
    int lastPos;

    //统一记录选中的所有图片
    private List<String> selectedImageBean = new ArrayList<>();

    protected class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RecyclerImageView mIvImageImage;
        AppCompatCheckBox mCbCheck;

        ImageView mIvCameraImage;
        ViewGroup parentView;

        public GridViewHolder(ViewGroup parentView, View itemView) {
            super(itemView);
            this.parentView = parentView;
            mIvImageImage = (RecyclerImageView) itemView.findViewById(R.id.iv_image_image);
            mCbCheck = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);

            mIvCameraImage = (ImageView) itemView.findViewById(R.id.iv_camera_image);

            mCbCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            //获取并处理当前点击位置
            int imagePos = getLayoutPosition();
            String imageBean = mImageBeanList.get(imagePos);

            if (!albumBuilder.isMutiSelect()) {

                //当前未选中时，清除上一个选中状态
                if (!selectedImageBean.contains(imageBean)) {
                    //清空图片选中容器
                    selectedImageBean.clear();
                }
                setRadioDisChecked(parentView);
                lastPos = imagePos;
            } else if (!selectedImageBean.contains(imageBean) && selectedImageBean.size() >= albumBuilder.getMaxSize()) {
                //选满时，添加无效，删除有效
                mCbCheck.setChecked(false);
                Toast.makeText(context, "最多选择" + albumBuilder.getMaxSize() + "", Toast.LENGTH_SHORT).show();
                return;
            }

            mCbCheck.setChecked(!selectedImageBean.contains(imageBean));
            notifySelectedImageBean(imageBean, !selectedImageBean.contains(imageBean));
            notifyItemChanged(imagePos);
        }

        /**
         * 设置未所有Item为未选中 单选用
         * @param parentView 父布局
         */
        private void setRadioDisChecked(ViewGroup parentView) {
            if (parentView == null || parentView.getChildCount() < 1) {
                return;
            }

            for (int i = 0; i < parentView.getChildCount(); i++) {
                View itemView = parentView.getChildAt(i);
                AppCompatCheckBox rbSelect = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);
                if(rbSelect != null) {
                    rbSelect.setChecked(false);
                }
            }
        }
    }

    private void notifySelectedImageBean(String imageBean, boolean isAdd)
    {
        //避免重复添加
        if (imageBean != null && !"".equals(imageBean)) {
            if (isAdd)
                selectedImageBean.add(imageBean);
            else
                selectedImageBean.remove(imageBean);
        } else return;

        Log.e("selectedImageBean", selectedImageBean.size() + "");
        RxBus.getDefault().postSticky(new ImageSelectedEvent(selectedImageBean));
        RxBus.getDefault().post(new ImageSelectedEvent(selectedImageBean));
    }
}