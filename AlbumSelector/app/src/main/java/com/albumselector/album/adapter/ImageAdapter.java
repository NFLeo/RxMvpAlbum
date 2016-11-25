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
import com.albumselector.album.entity.ImageBean;
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

        ImageBean imageBean = mImageBeanList.get(position);
        if(position == 0 && "".equals(imageBean.getImagePath())) {
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

            holder.mCbCheck.setChecked(imageBean.isSelected());

//            if(((PhotoActivity) context).getCheckedList() != null && ((PhotoActivity) context).getCheckedList().contains(ImageBean)){
//                holder.mCbCheck.setChecked(true);
//            } else {
//                holder.mCbCheck.setChecked(false);
//            }

            String path = imageBean.getImagePath();

            holder.mIvImageImage.setBackground(mImageViewBg);

            Glide.with(context)
                    .load(new File(path)).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.mIvImageImage);

//            Glide.with(context).load(new File(path)).crossFade().
//                    diskCacheStrategy(DiskCacheStrategy.NONE).
//                    into(holder.mIvImageImage);
        }
    }

    @Override
    public int getItemCount() {
        return mImageBeanList.size();
    }

    //记录上次选择，用于单选
    int lastPos;

    private void getSelected() {

        Observable.from(mImageBeanList)
                .filter(new Func1<ImageBean, Boolean>() {
            @Override
            public Boolean call(ImageBean imageBean) {
                return imageBean.isSelected();
            }
        }).flatMap(new Func1<ImageBean, Observable<List<ImageBean>>>() {
            @Override
            public Observable<List<ImageBean>> call(ImageBean imageBean) {
                List<ImageBean> selectImageBean = new ArrayList<ImageBean>();
                selectImageBean.add(imageBean);

                return Observable.just(selectImageBean);
            }
        }).subscribe(new Action1<List<ImageBean>>() {
            @Override
            public void call(List<ImageBean> imageBeen) {
                Log.e("SelectedImage", imageBeen.size() + "");
                RxBus.getDefault().postSticky(new ImageSelectedEvent(imageBeen));
                RxBus.getDefault().post(new ImageSelectedEvent(imageBeen));
            }
        });
    }

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
            setRadioDisChecked(parentView);
            int imagePos = getLayoutPosition();
            ImageBean imageBean = mImageBeanList.get(imagePos);
            mCbCheck.setChecked(!imageBean.isSelected());

            mImageBeanList.get(imagePos).setSelected(!imageBean.isSelected());
            mImageBeanList.get(lastPos).setSelected(false);

            lastPos = imagePos;

            notifyItemChanged(imagePos);

            getSelected();
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
