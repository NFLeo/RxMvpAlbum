package com.albumselector.album.ui;

import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.albumselector.R;
import com.albumselector.album.adapter.ImagePreviewAdapter;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.event.ImageSelectedEvent;
import com.albumselector.album.rxbus.event.KeyEvent;
import com.albumselector.album.ui.mvp.BaseMvpActivity;
import com.albumselector.album.ui.mvp.BasePresenter;
import com.albumselector.album.utils.AlbumBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;

/**
  * @desc:         图片预览界面
  * @author:       Leo
  * @date:         2016/11/25
  */
public class PhotoPreviewActivity extends BaseMvpActivity implements ViewPager.OnPageChangeListener
{
    private ViewPager mViewPager;
    private ImagePreviewAdapter imagePreviewAdapter;

    private List<String> selectedImage;                          //原始图片列表
    private int mPagerPosition;

    @Override
    protected BasePresenter createPresenterInstance() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_photo_preview;
    }

    @Override
    protected void onViewCreated() {
        initView();
        setView();
        setData();
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setCurrentItem(mPagerPosition, false);
        mViewPager.addOnPageChangeListener(this);
    }

    private void setListener() {
    }

    private void setData()
    {
        rxBusManager.add(RxBus.getDefault().toObservableSticky(ImageSelectedEvent.class)
                .filter(new Func1<ImageSelectedEvent, Boolean>() {
                    @Override
                    public Boolean call(ImageSelectedEvent imageSelectedEvent) {
                        return null != imageSelectedEvent.getImageBean();
                    }
                })
                .subscribe(new Action1<ImageSelectedEvent>() {
                    @Override
                    public void call(ImageSelectedEvent imageSelectedEvent) {
                        selectedImage = imageSelectedEvent.getImageBean();
                        imagePreviewAdapter = new ImagePreviewAdapter(context, selectedImage);
                        mViewPager.setAdapter(imagePreviewAdapter);
                    }
                }));
    }

    private void setView() {

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPagerPosition = position;
        String imageStr = selectedImage.get(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
