package com.albumselector.album.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.albumselector.R;
import com.albumselector.album.adapter.ImagePreviewAdapter;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.event.CloseImageViewPageFragmentEvent;
import com.albumselector.album.rxbus.event.ImageCheckChangeEvent;
import com.albumselector.album.rxbus.event.ImageViewPagerChangedEvent;
import com.albumselector.album.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc:
 * @author: Leo
 * @date: 2016/10/27
 */
public class ImagePreviewActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String EXTRA_PAGE_INDEX = EXTRA_PREFIX + ".PageIndex";

    DisplayMetrics mScreenSize;

    private AppCompatCheckBox mCbCheck;
    private ViewPager mViewPager;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<ImageBean> mImageBeanList;

    private int mPagerPosition;

    private PhotoActivity photoActivity;

    @Override
    public int getContentView() {
        return R.layout.fragment_photo_preview;
    }

    @Override
    protected void onCreateOk(@Nullable Bundle savedInstanceState)
    {
        mImageBeanList = new ArrayList<>();
        if(((PhotoActivity) context).getCheckedList() != null){
            mImageBeanList.addAll(photoActivity.getCheckedList());
        }
        imagePreviewAdapter = new ImagePreviewAdapter(context, mImageBeanList);
        mViewPager.setAdapter(imagePreviewAdapter);
        mCbCheck.setOnClickListener(this);

        if(savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX);
        }
    }

    @Override
    protected void initPrepare() {
//        photoActivity = getIntent().get
    }

    @Override
    public void findViews() {
        mCbCheck = (AppCompatCheckBox) findViewById(R.id.cb_check);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setCurrentItem(mPagerPosition, false);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPagerPosition = 0;
        RxBus.getDefault().post(new CloseImageViewPageFragmentEvent());
    }

    @Override
    public void onClick(View v) {
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
}
