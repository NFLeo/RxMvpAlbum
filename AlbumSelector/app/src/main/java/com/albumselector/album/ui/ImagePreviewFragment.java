package com.albumselector.album.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.albumselector.R;
import com.albumselector.album.adapter.ImagePreviewAdapter;
import com.albumselector.album.entity.Configuration;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.event.CloseImageViewPageFragmentEvent;
import com.albumselector.album.rxbus.event.ImageCheckChangeEvent;
import com.albumselector.album.rxbus.event.ImageViewPagerChangedEvent;
import com.albumselector.album.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Desction:图片预览
 * Author:pengjianbo
 * Date:16/6/9 上午1:35
 */
public class ImagePreviewFragment extends BaseFragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener{

    private static final String EXTRA_PAGE_INDEX = EXTRA_PREFIX + ".PageIndex";

    DisplayMetrics mScreenSize;

    private AppCompatCheckBox mCbCheck;
    private ViewPager mViewPager;
    private TextView mCropView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<ImageBean> mImageBeanList;

    private int mPagerPosition;

    public static ImagePreviewFragment newInstance(Configuration configuration, int position){
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CONFIGURATION, configuration);
        bundle.putInt(EXTRA_PAGE_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_photo_preview;
    }


    @Override
    protected void findView(View view) {
        mCbCheck = (AppCompatCheckBox) view.findViewById(R.id.cb_check);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mCropView = (TextView) view.findViewById(R.id.crop_image);
    }

    @Override
    public void onViewCreatedOk(View view, @Nullable Bundle savedInstanceState)
    {
        mImageBeanList = new ArrayList<>();
        if(photoActivity.getCheckedList() != null){
            mImageBeanList.addAll(photoActivity.getCheckedList());
        }
        imagePreviewAdapter = new ImagePreviewAdapter(context, mImageBeanList);
        mViewPager.setAdapter(imagePreviewAdapter);
        mCbCheck.setOnClickListener(this);

        if(savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }

        mCropView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setCurrentItem(mPagerPosition, false);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onFirstTimeLaunched() {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        if(outState != null){
            outState.putInt(EXTRA_PAGE_INDEX, mPagerPosition);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPagerPosition = position;
        ImageBean ImageBean = mImageBeanList.get(position);
        mCbCheck.setChecked(false);
        //判断是否选择
        if(photoActivity != null && photoActivity.getCheckedList() != null){
            mCbCheck.setChecked(photoActivity.getCheckedList().contains(ImageBean));
        }

        RxBus.getDefault().post(new ImageViewPagerChangedEvent(position, mImageBeanList.size(), true));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 改变选择
     * @param view
     */
    @Override
    public void onClick(View view) {
        int position = mViewPager.getCurrentItem();
        ImageBean ImageBean = mImageBeanList.get(position);
        if(mConfiguration.getMaxSize() == photoActivity.getCheckedList().size()
                && !photoActivity.getCheckedList().contains(ImageBean)) {
            Toast.makeText(context, getResources()
                    .getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()), Toast.LENGTH_SHORT).show();
            mCbCheck.setChecked(false);
        } else {
            RxBus.getDefault().post(new ImageCheckChangeEvent(ImageBean));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPagerPosition = 0;
        RxBus.getDefault().post(new CloseImageViewPageFragmentEvent());
    }
}
