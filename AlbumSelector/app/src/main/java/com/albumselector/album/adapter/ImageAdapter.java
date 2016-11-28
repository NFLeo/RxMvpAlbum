package com.albumselector.album.adapter;

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

import com.albumselector.R;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.rxbus.event.ImageSelectedEvent;
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

    private Context context;
    private List<String> mImageBeanList;
    private LayoutInflater mInflater;
    private int mImageSize;
    private Drawable mImageViewBg;
    private Drawable mCameraImage;
    private int mCameraTextColor;

    private boolean isSignalCheck;

    public ImageAdapter(Context context, List<String> list, int screenWidth) {
        this.context = context;
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
        if(position == 0 && "".equals(imageBean)) {
            holder.mCbCheck.setVisibility(View.GONE);
            holder.mIvImageImage.setVisibility(View.GONE);
            holder.mLlCamera.setVisibility(View.VISIBLE);
            holder.mIvCameraImage.setImageDrawable(mCameraImage);
            holder.mTvCameraTxt.setText("Camera");
            holder.mCbCheck.setVisibility(View.GONE);
        } else {
//            if(mConfiguration.isRadio()) {
//                holder.mCbCheck.setVisibility(View.GONE);
//            } else{
            holder.mCbCheck.setVisibility(View.VISIBLE);
//                holder.mCbCheck.setOnClickListener(new OnCheckBoxClickListener(imageBean));
//            }
            holder.mIvImageImage.setVisibility(View.VISIBLE);
            holder.mLlCamera.setVisibility(View.GONE);

            holder.mCbCheck.setChecked(selectedImageBean.contains(imageBean));

//            String path = imageBean.getImagePath();

            holder.mIvImageImage.setBackground(mImageViewBg);

            Glide.with(context)
                    .load(new File(imageBean)).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.mIvImageImage);
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

        LinearLayout mLlCamera;
        TextView mTvCameraTxt;
        ImageView mIvCameraImage;

        ViewGroup parentView;

        public GridViewHolder(ViewGroup parentView, View itemView) {
            super(itemView);
            this.parentView = parentView;
            mIvImageImage = (RecyclerImageView) itemView.findViewById(R.id.iv_image_image);
            mCbCheck = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);

            mLlCamera = (LinearLayout) itemView.findViewById(R.id.ll_camera);
            mTvCameraTxt = (TextView) itemView.findViewById(R.id.tv_camera_txt);
            mIvCameraImage = (ImageView) itemView.findViewById(R.id.iv_camera_image);

            mCbCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            //获取并处理当前点击位置
            int imagePos = getLayoutPosition();
            String imageBean = mImageBeanList.get(imagePos);

            if (!isSignalCheck) {

                //清空图片选中容器
                selectedImageBean.clear();

                setRadioDisChecked(parentView);
                //设置上一个view未选中状态
//                mImageBeanList.get(lastPos).setSelected(false);

                lastPos = imagePos;
            }

            mCbCheck.setChecked(!selectedImageBean.contains(imageBean));
            notifySelectedImageBean(imageBean, !selectedImageBean.contains(imageBean));

            //设置当前view状态
//            mImageBeanList.get(imagePos).setSelected(!imageBean.isSelected());

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