package com.albumselector.album.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.albumselector.R;
import com.albumselector.album.AsyncTask.BGAAsyncTask;
import com.albumselector.album.AsyncTask.BGALoadPhotoTask;
import com.albumselector.album.adapter.FolderAdapter;
import com.albumselector.album.adapter.ImageAdapter;
import com.albumselector.album.contract.PhotoContract;
import com.albumselector.album.entity.Configuration;
import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.model.PhotoModelImpl;
import com.albumselector.album.presenter.PhotoPresenterImpl;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.RxBusSubscriber;
import com.albumselector.album.rxbus.event.CloseImageViewPageFragmentEvent;
import com.albumselector.album.rxbus.event.ConstantsEvent;
import com.albumselector.album.rxbus.event.ImageCheckChangeEvent;
import com.albumselector.album.rxbus.event.OpenImagePageFragmentEvent;
import com.albumselector.album.rxbus.event.OpenImagePreviewFragmentEvent;
import com.albumselector.album.ui.base.BaseFragment;
import com.albumselector.album.utils.ImageFileManager;
import com.albumselector.album.utils.MediaScanner;
import com.albumselector.album.utils.anim.Animation;
import com.albumselector.album.utils.anim.AnimationListener;
import com.albumselector.album.utils.anim.SlideInUnderneathAnimation;
import com.albumselector.album.utils.anim.SlideOutUnderneathAnimation;
import com.albumselector.album.widget.FooterAdapter;
import com.albumselector.album.widget.HorizontalDividerItemDecoration;
import com.albumselector.album.widget.MarginDecoration;
import com.albumselector.album.widget.RecyclerViewFinal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @desc:
 * @author: Leo
 * @date: 2016/10/27
 */
public class ImageSelectFragment extends BaseFragment implements PhotoContract.View, 
        MediaScanner.ScanCallback, RecyclerViewFinal.OnLoadMoreListener,
        BGAAsyncTask.Callback<ArrayList<FolderBean>>,
        FolderAdapter.OnRecyclerViewItemClickListener, FooterAdapter.OnItemClickListener {

    public static final String TAG = ImageSelectFragment.class.getSimpleName();
    private final String FOLDER_ID_KEY = "FOLDER_ID_KEY";
    private final String IMAGE_STORE_FILE_NAME = "IMG_%s.jpg";

    private RecyclerViewFinal rvImage;
    private TextView tvReview;
    private TextView tvChooseCount;
    private RelativeLayout rlFolderOverview;
    private RecyclerView rvFolder;

    private MediaScanner mMediaScanner;

    private File mImageStoreDir;
    private String mImagePath;

    private PhotoPresenterImpl photoPresenter;
    private PhotoModelImpl photoModel;

    private FolderAdapter folderAdapter;                       //相册列表适配器
    private List<FolderBean> folderBeanList;                   //相册数据源

    private ImageAdapter imageAdapter;                       //照片列表适配器
    private List<ImageBean> imageBeanList;                    //照片数据源

    private Subscription mSubscrImageCheckChangeEvent;             //监听照片选择
    private Subscription mSubscrImageRefreshIndexEvent;            //监听照片分页查询分页序号
    private Subscription mSubscrCloseImageViewPageFragmentEvent;   //监听照片预览关闭事件

    private int PageIndex = 1;
    private int PageSize = 32;
    private String mFlolderId = String.valueOf(Integer.MIN_VALUE);

    public static ImageSelectFragment newInstance(Configuration configuration) {
        ImageSelectFragment fragment = new ImageSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CONFIGURATION, configuration);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mImageStoreDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/RxGalleryFinal/");
        mMediaScanner = new MediaScanner(context);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_photo_select;
    }

    @Override
    public void onViewCreatedOk(View view, @Nullable Bundle savedInstanceState) {
        photoPresenter = new PhotoPresenterImpl(getActivity());
        photoPresenter.setVM(this, photoModel);

        //获取相册数据
        photoPresenter.getFolderList();
        //获取默认相册中所有图片
        photoPresenter.getImageList(mFlolderId, PageIndex, PageSize);

        setView();
    }

    @Override
    protected void findView(View view)
    {
        rvImage = (RecyclerViewFinal) view.findViewById(R.id.rv_image);
        tvReview = (TextView) view.findViewById(R.id.tv_review);
        tvChooseCount = (TextView) view.findViewById(R.id.tv_choose_count);
        rlFolderOverview = (RelativeLayout) view.findViewById(R.id.rl_folder_overview);
        rvFolder = (RecyclerView) view.findViewById(R.id.rv_folder);
    }

    /**
     * 是否可以拍照
     */
    private boolean mTakePhotoEnabled;

    private BGALoadPhotoTask mLoadPhotoTask;

    @Override
    public void onStart() {
        super.onStart();
        new BGALoadPhotoTask(this, context, mTakePhotoEnabled);
    }

    private void setView()
    {
        //初始化图片墙
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvImage.addItemDecoration(new MarginDecoration(context));
        rvImage.setLayoutManager(gridLayoutManager);
        rvImage.setOnLoadMoreListener(this);
        rvImage.setFooterViewHide(true);

        imageBeanList = new ArrayList<>();
        imageAdapter = new ImageAdapter(context, imageBeanList, 1080);
        rvImage.setAdapter(imageAdapter);
        rvImage.setOnItemClickListener(this);

        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int id = v.getId();
                if(id == R.id.tv_review) {
                    v.setEnabled(false);
                    int visibility = rlFolderOverview.getVisibility();
                    if(visibility == View.VISIBLE) {
                        new SlideOutUnderneathAnimation(rvFolder)
                                .setDirection(Animation.DIRECTION_DOWN)
                                .setDuration(Animation.DURATION_DEFAULT)
                                .setListener(new AnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        v.setEnabled(true);
                                        rlFolderOverview.setVisibility(View.GONE);
                                    }
                                })
                                .animate();
                    } else {
                        rlFolderOverview.setVisibility(View.VISIBLE);
                        new SlideInUnderneathAnimation(rvFolder)
                                .setDirection(Animation.DIRECTION_DOWN)
                                .setDuration(Animation.DURATION_DEFAULT)
                                .setListener(new AnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        v.setEnabled(true);
                                    }
                                })
                                .animate();
                    }
                } else {
                    new SlideOutUnderneathAnimation(rvFolder)
                            .setDirection(Animation.DIRECTION_DOWN)
                            .setDuration(Animation.DURATION_DEFAULT)
                            .setListener(new AnimationListener() {
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    v.setEnabled(true);
                                    rlFolderOverview.setVisibility(View.GONE);
                                }
                            })
                            .animate();
                }
            }
        });

        tvChooseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().post(new OpenImagePreviewFragmentEvent());
            }
        });

        //初始化相册列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvFolder.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                .color(getResources().getColor(R.color.gallery_bucket_list_decoration_color))
                .size(getResources().getDimensionPixelSize(R.dimen.gallery_divider_decoration_height))
                .margin(getResources().getDimensionPixelSize(R.dimen.gallery_bucket_margin),
                        getResources().getDimensionPixelSize(R.dimen.gallery_bucket_margin))
                .build());

        rvFolder.setLayoutManager(linearLayoutManager);

        //配置相册适配器
        folderBeanList = new ArrayList<>();
        folderAdapter = new FolderAdapter(context, folderBeanList);
        folderAdapter.setOnRecyclerViewItemClickListener(this);
        rvFolder.setAdapter(folderAdapter);

        rlFolderOverview.setVisibility(View.INVISIBLE);
        new SlideInUnderneathAnimation(rvFolder)
                .setDirection(Animation.DIRECTION_DOWN)
                .animate();

        subscribeEvent();
    }

    @Override
    protected void onFirstTimeLaunched() {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(!TextUtils.isEmpty(mFlolderId)) {
            outState.putString(FOLDER_ID_KEY, mFlolderId);
        }
    }

    @Override
    public void imageCallback(List<ImageBean> list) {
        if(!mConfiguration.isHideCamera()) {
            if (PageIndex == 1 && TextUtils.equals(mFlolderId, String.valueOf(Integer.MIN_VALUE))) {
                ImageBean takePhotoBean = new ImageBean();
                takePhotoBean.setImageId(Integer.MIN_VALUE);
                takePhotoBean.setImageFolderId(String.valueOf(Integer.MIN_VALUE));
                imageBeanList.add(takePhotoBean);
            }
        }
        if (list != null && list.size() > 0) {
            imageBeanList.addAll(list);
            Log.i("TAG", String.format("得到:%s张图片", list.size()));
        } else {
            Log.i("TAG", "没有更多图片");
        }
        imageAdapter.notifyDataSetChanged();

        if (ConstantsEvent.ImageRefreshIndex != 0)
            PageIndex = ConstantsEvent.ImageRefreshIndex;

        PageIndex++;

        System.out.println("PageIndex++" + PageIndex++);
        if (list == null || list.size() < PageSize) {
            rvImage.setFooterViewHide(true);
            rvImage.setHasLoadMore(false);
        } else {
            rvImage.setFooterViewHide(false);
            rvImage.setHasLoadMore(true);
        }

        if (imageBeanList.size() == 0) {
//            String mediaEmptyTils = ThemeUtils.resolveString(getContext(), R.attr.gallery_media_empty_tips, R.string.gallery_default_media_empty_tips);
//            EmptyViewUtils.showMessage(mLlEmptyView, mediaEmptyTils);
        }

        rvImage.onLoadMoreComplete();
    }

    @Override
    public void folderCallback(List<FolderBean> list) {
        if(list == null || list.size() == 0){
            return;
        }

        folderBeanList.addAll(list);
        folderAdapter.setSelectedBucket(list.get(0));               //初始标记默认选中所有相册
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        mFlolderId = savedInstanceState.getString(FOLDER_ID_KEY);
    }

    @Override
    public void errorCallback() {

    }

    @Override
    public void loadMore() {
        photoPresenter.getImageList(mFlolderId, PageIndex, PageSize);
    }

    @Override
    public void onItemClick(View view, int position)
    {
        FolderBean bucketBean = folderBeanList.get(position);
        String bucketId = bucketBean.getFolderId();

        new SlideOutUnderneathAnimation(rvFolder)
                .setDirection(Animation.DIRECTION_DOWN)
                .setDuration(Animation.DURATION_DEFAULT)
                .setListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rlFolderOverview.setEnabled(true);
                        rlFolderOverview.setVisibility(View.GONE);
                    }
                })
                .animate();

        if(TextUtils.equals(mFlolderId, bucketId)){
            return;
        }
        mFlolderId = bucketId;
        rvImage.setHasLoadMore(false);
        imageBeanList.clear();
        imageAdapter.notifyDataSetChanged();
        folderAdapter.setSelectedBucket(bucketBean);

        rvImage.setFooterViewHide(true);
        PageIndex = 1;
        photoPresenter.getImageList(mFlolderId, PageIndex, PageSize);
    }

    private void subscribeEvent()
    {
        mSubscrImageCheckChangeEvent = RxBus.getDefault().toObservable(ImageCheckChangeEvent.class)
                .subscribe(new RxBusSubscriber<ImageCheckChangeEvent>() {
                    @Override
                    protected void onEvent(ImageCheckChangeEvent mediaCheckChangeEvent) {
//                        if(mMediaActivity.getCheckedList().size() == 0){
//                            mTvPreview.setEnabled(false);
//                        } else {
//                            mTvPreview.setEnabled(true);
//                        }
                    }
                });
        RxBus.getDefault().add(mSubscrImageCheckChangeEvent);

        mSubscrCloseImageViewPageFragmentEvent = RxBus.getDefault().toObservable(CloseImageViewPageFragmentEvent.class)
                .subscribe(new RxBusSubscriber<CloseImageViewPageFragmentEvent>() {
                    @Override
                    protected void onEvent(CloseImageViewPageFragmentEvent closeMediaViewPageFragmentEvent) throws Exception {
                        imageAdapter.notifyDataSetChanged();
                    }
                });
        RxBus.getDefault().add(mSubscrCloseImageViewPageFragmentEvent);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
        ImageBean imageBean = imageBeanList.get(position);
        if (imageBean.getImageId() == Integer.MIN_VALUE) {

            openCamera();

        } else {
            if (mConfiguration.isRadio()) {
//                radioNext(ImageBean);
            } else {
                ImageBean firstBean = imageBeanList.get(0);
                ArrayList<ImageBean> gridMediaList = new ArrayList<>();
                gridMediaList.addAll(imageBeanList);
                int pos = position;
                if(firstBean.getImageId() == Integer.MIN_VALUE) {
                    pos = position - 1;
                    gridMediaList.clear();
                    List<ImageBean> list = imageBeanList.subList(1, imageBeanList.size());
                    gridMediaList.addAll(list);
                }
                RxBus.getDefault().post(new OpenImagePageFragmentEvent(gridMediaList, pos));
            }
        }
    }

    private String mCurrentPhotoPath;
    private File mImageDir;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", mImageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Intent getTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        return takePictureIntent;
    }

    private void openCamera() {


        try {
            startActivityForResult(getTakePictureIntent(), TAKE_IMAGE_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
//            String filename = String.format(IMAGE_STORE_FILE_NAME, dateFormat.format(new Date()));
//            mImagePath = new File(mImageStoreDir, filename).getAbsolutePath();
//            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath)));
//            startActivityForResult(captureIntent, TAKE_IMAGE_REQUEST_CODE);
//        } else {
////            Toast.makeText(getContext(), R.string.gallery_device_camera_unable, Toast.LENGTH_SHORT).show();
//        }
    }

    private final int TAKE_IMAGE_REQUEST_CODE = 1001;
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            ImageBean cameraPath = new ImageBean(mImagePath);
            imageBeanList.add(0, cameraPath);
            imageAdapter.notifyDataSetChanged();

            //刷新相册数据库
            mMediaScanner.scanFile(mImagePath, "image/jpeg", this);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getDefault().remove(mSubscrImageCheckChangeEvent);
        RxBus.getDefault().remove(mSubscrCloseImageViewPageFragmentEvent);
    }

    @Override
    public void onScanCompleted(String[] images) {
        if(images == null || images.length == 0){
            return;
        }

        rx.Observable.create(new rx.Observable.OnSubscribe<List<ImageBean>>() {
            @Override
            public void call(Subscriber<? super List<ImageBean>> subscriber) {
                List<ImageBean> imageBean = ImageFileManager.loadAllImage(context, mFlolderId, PageIndex, PageSize);
                Log.e("TIME", "time XX");
                subscriber.onNext(imageBean);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorCallback();
                    }

                    @Override
                    public void onNext(List<ImageBean> imageBeen) {
                        Log.e("TIME", "time XXX");
                        imageCallback(imageBeen);
                    }
                });
    }

    @Override
    public void onPostExecute(ArrayList<FolderBean> folderBeen) {
        mLoadPhotoTask = null;
    }

    @Override
    public void onTaskCancelled() {
        mLoadPhotoTask = null;
    }
}
