package com.albumselector.album.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albumselector.R;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.event.ImageCheckChangeEvent;
import com.albumselector.album.ui.PhotoActivity;
import com.albumselector.album.widget.RecyclerImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/18 下午7:48
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.GridViewHolder> {

    private Context context;
    private List<ImageBean> mImageBeanList;
    private LayoutInflater mInflater;
    private int mImageSize;
    private Drawable mImageViewBg;
    private Drawable mCameraImage;
    private int mCameraTextColor;

    public ImageAdapter(Context context, List<ImageBean> list, int screenWidth) {
        this.context = context;
        this.mImageBeanList = list;
        this.mInflater = LayoutInflater.from(context);
        this.mImageSize = screenWidth/3;

//        this.mImageViewBg = ThemeUtils.resolveDrawable(mMediaActivity,
//                R.attr.gallery_imageview_bg, R.drawable.gallery_default_image);
//        this.mCameraImage = ThemeUtils.resolveDrawable(mMediaActivity, R.attr.gallery_camera_bg,
//                R.drawable.gallery_ic_camera);
//        this.mCameraTextColor = ThemeUtils.resolveColor(mMediaActivity, R.attr.gallery_take_image_text_color,
//                R.color.gallery_default_take_image_text_color);
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image_list, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        ImageBean ImageBean = mImageBeanList.get(position);
        if(ImageBean.getImageId() == Integer.MIN_VALUE) {
            holder.mCbCheck.setVisibility(View.GONE);
            holder.mIvImageImage.setVisibility(View.GONE);
            holder.mLlCamera.setVisibility(View.VISIBLE);
            holder.mIvCameraImage.setImageDrawable(mCameraImage);
            holder.mTvCameraTxt.setText("Camera");
        } else {
//            if(mConfiguration.isRadio()) {
//                holder.mCbCheck.setVisibility(View.GONE);
//            } else{
                holder.mCbCheck.setVisibility(View.VISIBLE);
                holder.mCbCheck.setOnClickListener(new OnCheckBoxClickListener(ImageBean));
//            }
            holder.mIvImageImage.setVisibility(View.VISIBLE);
            holder.mLlCamera.setVisibility(View.GONE);
            if(((PhotoActivity) context).getCheckedList() != null && ((PhotoActivity) context).getCheckedList().contains(ImageBean)){
                holder.mCbCheck.setChecked(true);
            } else {
                holder.mCbCheck.setChecked(false);
            }

            String path = ImageBean.getImagePath();

            holder.mIvImageImage.setBackground(mImageViewBg);

            Glide.with(context).load(new File(path)).asBitmap().into(holder.mIvImageImage);
        }
    }

    @Override
    public int getItemCount() {
        return mImageBeanList.size();
    }

    class OnCheckBoxClickListener implements View.OnClickListener {

        private ImageBean ImageBean;

        public OnCheckBoxClickListener(ImageBean bean) {
            this.ImageBean = bean;
        }

        @Override
        public void onClick(View view) {
            if(9 == ((PhotoActivity) context).getCheckedList().size() &&
                    !((PhotoActivity) context).getCheckedList().contains(ImageBean)) {
                AppCompatCheckBox checkBox = (AppCompatCheckBox) view;
                checkBox.setChecked(false);
                Toast.makeText(context, context.getResources()
                        .getString(R.string.gallery_image_max_size_tip, 9), Toast.LENGTH_SHORT).show();
            } else {
                RxBus.getDefault().post(new ImageCheckChangeEvent(ImageBean));
            }
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        RecyclerImageView mIvImageImage;
        AppCompatCheckBox mCbCheck;

        LinearLayout mLlCamera;
        TextView mTvCameraTxt;
        ImageView mIvCameraImage;

        public GridViewHolder(View itemView) {
            super(itemView);
            mIvImageImage = (RecyclerImageView) itemView.findViewById(R.id.iv_image_image);
            mCbCheck = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);

            mLlCamera = (LinearLayout) itemView.findViewById(R.id.ll_camera);
            mTvCameraTxt = (TextView) itemView.findViewById(R.id.tv_camera_txt);
            mIvCameraImage = (ImageView) itemView.findViewById(R.id.iv_camera_image);
        }
    }

}
