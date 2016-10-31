package com.albumselector.album.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.albumselector.R;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.RxBusSubscriber;
import com.albumselector.album.rxbus.event.CloseRxImageGridPageEvent;
import com.albumselector.album.rxbus.event.ImageCheckChangeEvent;
import com.albumselector.album.rxbus.event.ImageViewPagerChangedEvent;
import com.albumselector.album.rxbus.event.OpenImagePageFragmentEvent;
import com.albumselector.album.rxbus.event.OpenImagePreviewFragmentEvent;
import com.albumselector.album.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Func1;

/**
 * @desc:
 * @author: Leo
 * @date: 2016/10/27
 */
public class PhotoActivity extends BaseActivity implements PhotoFragmentView<ImageBean>
{
    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;

    private static final String EXTRA_CHECKED_LIST = EXTRA_PREFIX + ".CheckedList";
    private static final String EXTRA_SELECTED_INDEX = EXTRA_PREFIX + ".SelectedIndex";
    private static final String EXTRA_PAGE_Image_LIST = EXTRA_PREFIX + ".PageImageList";
    private static final String EXTRA_PAGE_POSITION = EXTRA_PREFIX + ".PagePosition";
    private static final String EXTRA_PREVIEW_POSITION = EXTRA_PREFIX + ".PreviewPosition";

    private ImageFragment imageFragment;

    private ArrayList<ImageBean> checkedList;
    private int selectedIndex = 0;
    private ArrayList<ImageBean> pageImageList;
    private int pagePosition;
    private int previewPosition;

    @Override
    public int getContentView() {
        return R.layout.activity_photo_select;
    }

    @Override
    protected void onCreateOk(@Nullable Bundle savedInstanceState)
    {
        imageFragment = ImageFragment.newInstance(mConfiguration);

//        if(!mConfiguration.isRadio()) {
//            btnSubmit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(checkedList != null && checkedList.size() > 0) {
//                        BaseResultEvent event = new ImageMultipleResultEvent(checkedList);
//                        RxBus.getDefault().post(event);
//                        finish();
//                    }
//                }
//            });
//            btnSubmit.setVisibility(View.VISIBLE);
//        } else {
//            btnSubmit.setVisibility(View.GONE);
//        }

        checkedList = new ArrayList<>();
        List<ImageBean> selectedList = mConfiguration.getSelectedList();
        if(selectedList != null && selectedList.size() > 0){
            checkedList.addAll(selectedList);
        }

        showImageGridFragment();
        subscribeEvent();
    }

    @Override
    protected void initPrepare() {

    }

    @Override
    public void findViews() {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if(checkedList != null) {
            outState.putParcelableArrayList(EXTRA_CHECKED_LIST, checkedList);
        }
        outState.putInt(EXTRA_SELECTED_INDEX, selectedIndex);
        if(pageImageList != null) {
            outState.putParcelableArrayList(EXTRA_PAGE_Image_LIST, pageImageList);
        }
        outState.putInt(EXTRA_PAGE_POSITION, pagePosition);
        outState.putInt(EXTRA_PREVIEW_POSITION, previewPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        List<ImageBean> list = savedInstanceState.getParcelableArrayList(EXTRA_CHECKED_LIST);
        if(list != null && list.size() > 0){
            checkedList.clear();
            checkedList.addAll(list);
        }
        pageImageList = savedInstanceState.getParcelableArrayList(EXTRA_PAGE_Image_LIST);
        pagePosition = savedInstanceState.getInt(EXTRA_PAGE_POSITION);
        previewPosition = savedInstanceState.getInt(EXTRA_PREVIEW_POSITION);
        selectedIndex = savedInstanceState.getInt(EXTRA_SELECTED_INDEX);
        if(!mConfiguration.isRadio()) {
            switch (selectedIndex) {
                case 1:
                    showImagePageFragment(pageImageList, pagePosition);
                    break;
                case 2:
                    showImagePreviewFragment();
                    break;
            }
        }
    }

    @Override
    public void showImageGridFragment()
    {
        selectedIndex = 0;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, imageFragment);
//        if(mImagePreviewFragment != null) {
//            ft.hide(mImagePreviewFragment);
//        }
//        if(mImagePageFragment != null){
//            ft.hide(mImagePageFragment);
//        }
        ft.show(imageFragment).commit();
    }

    @Override
    public void showImagePageFragment(ArrayList<ImageBean> list, int position) {

    }

    @Override
    public void showImagePreviewFragment() {

    }

    private void subscribeEvent() {
        Subscription subscriptionOpenImagePreviewEvent = RxBus.getDefault().toObservable(OpenImagePreviewFragmentEvent.class)
                .map(new Func1<OpenImagePreviewFragmentEvent, OpenImagePreviewFragmentEvent>() {
                    @Override
                    public OpenImagePreviewFragmentEvent call(OpenImagePreviewFragmentEvent openImagePreviewFragmentEvent) {
                        return openImagePreviewFragmentEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<OpenImagePreviewFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenImagePreviewFragmentEvent o) throws Exception {
                        previewPosition = 0;
                        showImagePreviewFragment();
                    }
                });

        RxBus.getDefault().add(subscriptionOpenImagePreviewEvent);

        Subscription subscriptionImageCheckChangeEvent = RxBus.getDefault().toObservable(ImageCheckChangeEvent.class)
                .map(new Func1<ImageCheckChangeEvent, ImageCheckChangeEvent>() {
                    @Override
                    public ImageCheckChangeEvent call(ImageCheckChangeEvent ImageCheckChangeEvent) {
                        return ImageCheckChangeEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<ImageCheckChangeEvent>() {
                    @Override
                    protected void onEvent(ImageCheckChangeEvent ImageCheckChangeEvent) {
                        ImageBean ImageBean = ImageCheckChangeEvent.getImageBean();
                        if(checkedList.contains(ImageBean)) {
                            checkedList.remove(ImageBean);
                        } else {
                            checkedList.add(ImageBean);
                        }

                        if(checkedList.size() > 0){
                            String text = getResources().getString(R.string.gallery_over_button_text_checked, checkedList.size(), mConfiguration.getMaxSize());
//                            btnSubmit.setText(text);
//                            btnSubmit.setEnabled(true);
                        } else {
//                            btnSubmit.setText(R.string.gallery_over_button_text);
//                            btnSubmit.setEnabled(false);
                        }
                    }
                });
        RxBus.getDefault().add(subscriptionImageCheckChangeEvent);

        Subscription subscriptionImageViewPagerChangedEvent = RxBus.getDefault().toObservable(ImageViewPagerChangedEvent.class)
                .map(new Func1<ImageViewPagerChangedEvent, ImageViewPagerChangedEvent>() {
                    @Override
                    public ImageViewPagerChangedEvent call(ImageViewPagerChangedEvent ImageViewPagerChangedEvent) {
                        return ImageViewPagerChangedEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<ImageViewPagerChangedEvent>() {
                    @Override
                    protected void onEvent(ImageViewPagerChangedEvent ImagePreviewViewPagerChangedEvent) {
                        int curIndex = ImagePreviewViewPagerChangedEvent.getCurIndex();
                        int totalSize = ImagePreviewViewPagerChangedEvent.getTotalSize();
                        if(ImagePreviewViewPagerChangedEvent.isPreview()) {
                            previewPosition = curIndex;
                        } else {
                            pagePosition = curIndex;
                        }
                    }
                });
        RxBus.getDefault().add(subscriptionImageViewPagerChangedEvent);

        Subscription subscriptionCloseRxImageGridPageEvent = RxBus.getDefault().toObservable(CloseRxImageGridPageEvent.class)
                .subscribe(new RxBusSubscriber<CloseRxImageGridPageEvent>() {
                    @Override
                    protected void onEvent(CloseRxImageGridPageEvent closeRxImageGridPageEvent) throws Exception {
                        finish();
                    }
                });
        RxBus.getDefault().add(subscriptionCloseRxImageGridPageEvent);

        Subscription subscriptionOpenImagePageFragmentEvent = RxBus.getDefault().toObservable(OpenImagePageFragmentEvent.class)
                .subscribe(new RxBusSubscriber<OpenImagePageFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenImagePageFragmentEvent openImagePageFragmentEvent) {
                        pageImageList = openImagePageFragmentEvent.getImageBeanList();
                        pagePosition = openImagePageFragmentEvent.getPosition();

                        showImagePageFragment(pageImageList, pagePosition);
                    }
                });
        RxBus.getDefault().add(subscriptionOpenImagePageFragmentEvent);
    }

    public List<ImageBean> getCheckedList() {
        return checkedList;
    }
}
