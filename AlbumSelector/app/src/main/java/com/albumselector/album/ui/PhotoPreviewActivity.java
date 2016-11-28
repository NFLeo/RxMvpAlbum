package com.albumselector.album.ui;

import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.widget.TextView;

import com.albumselector.R;
import com.albumselector.album.adapter.ImagePreviewAdapter;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.event.ImageSelectedEvent;
import com.albumselector.album.rxbus.event.KeyEvent;
import com.albumselector.album.ui.mvp.BaseMvpActivity;
import com.albumselector.album.ui.mvp.BasePresenter;

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
    private AppCompatCheckBox mCbCheck;
    private ViewPager mViewPager;
    private TextView mCropView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<ImageBean> mImageBeanList;

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

    private void setListener() {

    }

    private void setData() {
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
                        imagePreviewAdapter = new ImagePreviewAdapter(context, imageSelectedEvent.getImageBean());
                        mViewPager.setAdapter(imagePreviewAdapter);
                    }
                }));

        rxBusManager.add(RxBus.getDefault().toObservableSticky(KeyEvent.class)
                .filter(new Func1<KeyEvent, Boolean>() {
                    @Override
                    public Boolean call(KeyEvent keyEvent) {
                        if (keyEvent.getKey() == KeyEvent.PHOTO_CAMERA) {
                            return keyEvent.getValue() != null;
                        }
                        return false;
                    }
                })
                .subscribe(new Action1<KeyEvent>() {
                    @Override
                    public void call(KeyEvent image) {
                        String imageStr = "";
                        if (KeyEvent.PHOTO_CAMERA == image.getKey())
                            imageStr = (String) image.getValue();

                        Log.e("imageStr", imageStr);
                        ImageBean imageBean = new ImageBean(imageStr, true);
                        List<String> imageBeanList = new ArrayList<String>();
                        imageBeanList.add(imageStr);
                        imagePreviewAdapter = new ImagePreviewAdapter(context, imageBeanList);
                        mViewPager.setAdapter(imagePreviewAdapter);
                    }
                }));
    }

    private void setView() {

    }

    private void initView() {
        mCbCheck = (AppCompatCheckBox) findViewById(R.id.cb_check);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCropView = (TextView) findViewById(R.id.crop_image);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
