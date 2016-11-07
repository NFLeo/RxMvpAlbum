package com.albumselector.album.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.albumselector.R;
import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.widget.SquareImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/4 下午5:40
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.BucketViewHolder> {

    private List<FolderBean> mBucketList;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private FolderBean mSelectedBucket;

    public FolderAdapter(Context context, List<FolderBean> bucketList) {
        this.mContext = context;
        this.mBucketList = bucketList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_album_folder, parent, false);
        return new BucketViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(BucketViewHolder holder, int position) {
        FolderBean bucketBean = mBucketList.get(position);

        if (bucketBean == null)
            return;

        String bucketName = bucketBean.getFolderName();

        holder.mTvBucketName.setText(bucketName + "\n" + bucketBean.getFolderSize() + "张");

        if(mSelectedBucket != null && TextUtils.equals(mSelectedBucket.getFolderId(), bucketBean.getFolderId())) {
            holder.mRbSelected.setVisibility(View.VISIBLE);
            holder.mRbSelected.setChecked(true);
        } else {
            holder.mRbSelected.setVisibility(View.GONE);
        }

        String path = bucketBean.getFolderCover();

        Glide.with(mContext).load(new File(path)).asBitmap().into(holder.mIvBucketCover);
    }

    public void setSelectedBucket(FolderBean bucketBean) {
        this.mSelectedBucket = bucketBean;
    }

    @Override
    public int getItemCount() {
        return mBucketList.size();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnRecyclerViewItemClickListener = listener;
    }

    class BucketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTvBucketName;
        SquareImageView mIvBucketCover;
        AppCompatRadioButton mRbSelected;

        private ViewGroup mParentView;

        public BucketViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            this.mParentView = parent;
            mTvBucketName = (TextView) itemView.findViewById(R.id.tv_bucket_name);
            mIvBucketCover = (SquareImageView) itemView.findViewById(R.id.iv_bucket_cover);
            mRbSelected = (AppCompatRadioButton) itemView.findViewById(R.id.rb_selected);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mOnRecyclerViewItemClickListener != null) {
                mOnRecyclerViewItemClickListener.onItemClick(v, getLayoutPosition());
            }

            setRadioDisChecked(mParentView);
            mRbSelected.setVisibility(View.VISIBLE);
            mRbSelected.setChecked(true);
        }

        /**
         * 设置未所有Item为未选中
         * @param parentView
         */
        private void setRadioDisChecked(ViewGroup parentView) {
            if (parentView == null || parentView.getChildCount() < 1) {
                return;
            }

            for (int i = 0; i < parentView.getChildCount(); i++) {
                View itemView = parentView.getChildAt(i);
                RadioButton rbSelect = (RadioButton) itemView.findViewById(R.id.rb_selected);
                if(rbSelect!=null){
                    rbSelect.setVisibility(View.GONE);
                    rbSelect.setChecked(false);
                }
            }
        }
    }

    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, int position);
    }
}