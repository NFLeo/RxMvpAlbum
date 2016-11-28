package com.albumselector.album.ui;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.albumselector.R;
import com.albumselector.album.AsyncTask.BGAAsyncTask;
import com.albumselector.album.AsyncTask.BGALoadPhotoTask;
import com.albumselector.album.adapter.FolderAdapter;
import com.albumselector.album.adapter.ImageAdapter;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.model.PhotoModelImpl;
import com.albumselector.album.presenter.PhotoPickerPresenterImpl;
import com.albumselector.album.presenter.PhotoPresenterImpl;
import com.albumselector.album.rxbus.event.ImageSelectedEvent;
import com.albumselector.album.rxbus.event.KeyEvent;
import com.albumselector.album.rxbus.event.OpenImagePreviewFragmentEvent;
import com.albumselector.album.ui.mvp.BaseMvpActivity;
import com.albumselector.album.ui.mvp.BasePresenter;
import com.albumselector.album.utils.ImagePickerManager;
import com.albumselector.album.utils.MediaScanner;
import com.albumselector.album.utils.anim.Animation;
import com.albumselector.album.utils.anim.AnimationListener;
import com.albumselector.album.utils.anim.SlideInUnderneathAnimation;
import com.albumselector.album.utils.anim.SlideOutUnderneathAnimation;
import com.albumselector.album.widget.FooterAdapter;
import com.albumselector.album.widget.HorizontalDividerItemDecoration;
import com.albumselector.album.widget.MarginDecoration;
import com.albumselector.album.widget.RecyclerViewFinal;
import com.albumselector.album.widget.cropimage.CropActivity;
import com.albumselector.album.widget.cropimage.CropFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

 /**
  * @desc:         图片选择界面
  * @author:       Leo
  * @date:         2016/11/28
  */
public class PhotoPickerActivity extends BaseMvpActivity implements BGAAsyncTask.Callback<ArrayList<FolderBean>>,
        FolderAdapter.OnRecyclerViewItemClickListener, FooterAdapter.OnItemClickListener, View.OnClickListener
{
    //是否可以拍照
    private boolean mTakePhotoEnabled = true;

    //拍照的请求码
    private static final int REQUEST_CODE_TAKE_PHOTO = 111;
    //裁剪的请求码
    private static final int REQUEST_CODE_CROP_PHOTO = 222;

    private BGALoadPhotoTask mLoadPhotoTask;

    private RecyclerViewFinal rvImage;
    private TextView tvReview;
    private TextView tvChooseCount;
    private RelativeLayout rlFolderOverview;
    private RecyclerView rvFolder;

    private MediaScanner mMediaScanner;

    private File mImageStoreDir;
    private String mImagePath;
    private ImagePickerManager imagePickerManager;

    private PhotoPresenterImpl photoPresenter;
    private PhotoModelImpl photoModel;

    private FolderAdapter folderAdapter;                       //相册列表适配器
    private List<FolderBean> folderBeanList;                   //相册数据源

    private ImageAdapter imageAdapter;                         //照片列表适配器
    private List<String> imageBeanList;                     //照片数据源
    private List<String> selectImageBeanList;               //已选照片

    private Subscription mSubscrImageCheckChangeEvent;             //监听照片选择
    private Subscription mSubscrImageRefreshIndexEvent;            //监听照片分页查询分页序号
    private Subscription mSubscrCloseImageViewPageFragmentEvent;   //监听照片预览关闭事件

    private int PageIndex = 1;
    private int PageSize = 32;
    private String mFlolderId = String.valueOf(Integer.MIN_VALUE);

    @Override
    public void onStart() {
        super.onStart();
        mLoadPhotoTask = new BGALoadPhotoTask(this, context, mTakePhotoEnabled).perform();
    }

    @Override
    protected BasePresenter createPresenterInstance() {
        presenter = new PhotoPickerPresenterImpl(getActivity());
        presenter.attachView(this);
        return presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_photo_select;
    }

    @Override
    protected void onViewCreated() {
        initView();
        setView();
        setData();
        setListener();
    }

    private void setData() {

        if (mTakePhotoEnabled) {
            File imageDir = new File(Environment.getExternalStorageDirectory(), "Hulabanban");
            imagePickerManager = new ImagePickerManager(this, imageDir);
        }

        rxBusManager.add(RxBus.getDefault().toObservable(ImageSelectedEvent.class)
                .filter(new Func1<ImageSelectedEvent, Boolean>() {
                    @Override
                    public Boolean call(ImageSelectedEvent imageSelectedEvent) {
                        return imageSelectedEvent.getImageBean() != null;
                    }
                })
                .subscribe(new Action1<ImageSelectedEvent>() {
                    @Override
                    public void call(ImageSelectedEvent imageSelectedEvent) {
                        selectImageBeanList = imageSelectedEvent.getImageBean();
                    }
                }));
    }

    private void initView()
    {
        rvImage = (RecyclerViewFinal) findViewById(R.id.rv_image);
        tvReview = (TextView) findViewById(R.id.tv_review);
        tvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
        rlFolderOverview = (RelativeLayout) findViewById(R.id.rl_folder_overview);
        rvFolder = (RecyclerView) findViewById(R.id.rv_folder);
    }

    private void setView()
    {
        //初始化图片墙
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvImage.addItemDecoration(new MarginDecoration(context));
        rvImage.setLayoutManager(gridLayoutManager);

        imageBeanList = new ArrayList<>();
        imageAdapter = new ImageAdapter(context, imageBeanList, 1080);
        rvImage.setAdapter(imageAdapter);
        rvImage.setOnItemClickListener(this);

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

//        subscribeEvent();
    }

    @Override
    public void onPostExecute(ArrayList<FolderBean> folderBeen)
    {
        mLoadPhotoTask = null;
        folderBeanList.clear();
        folderBeanList.addAll(folderBeen);
        folderAdapter.setSelectedBucket(folderBeen.get(0));               //初始标记默认选中所有相册
        folderAdapter.notifyDataSetChanged();

        imageBeanList.clear();


        imageBeanList.addAll(folderBeen.get(0).getImages());
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskCancelled() {
        mLoadPhotoTask = null;
    }

    @Override
    public void onItemClick(View view, int position)
    {
        FolderBean bucketBean = folderBeanList.get(position);
        String bucketId = bucketBean.getFolderId();

        //记录相册选中状态
        bucketBean.setCheck(true);
        folderAdapter.setSelectedBucket(bucketBean, position);

        hideFolderView(view);

        imageBeanList.clear();

        imageBeanList.addAll(bucketBean.getImages());
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position)
    {
        if (mTakePhotoEnabled && position == 0)
        {
            try {
                startActivityForResult(imagePickerManager.getTakePictureIntent(), REQUEST_CODE_TAKE_PHOTO);
            } catch (IOException e) {
                Log.e("Camera", "Camera error");
            }
        }
    }

    private void showFolderView(final View view)
    {
        rlFolderOverview.setVisibility(View.VISIBLE);

        new SlideInUnderneathAnimation(rvFolder)
                .setDirection(Animation.DIRECTION_DOWN)
                .setDuration(Animation.DURATION_DEFAULT)
                .setListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setEnabled(true);
                    }
                })
                .animate();
    }

    private void hideFolderView(final View view)
    {
        if (rlFolderOverview.getVisibility() == View.VISIBLE) {
            new SlideOutUnderneathAnimation(rvFolder)
                    .setDirection(Animation.DIRECTION_DOWN)
                    .setDuration(Animation.DURATION_DEFAULT)
                    .setListener(new AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (view != null) {
                                view.setEnabled(true);
                            }
                            rlFolderOverview.setVisibility(View.GONE);
                        }
                    })
                    .animate();
        }
    }

    private void setListener()
    {
        rootView.setOnClickListener(this);
        tvReview.setOnClickListener(this);
        tvChooseCount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_review :
                if(rlFolderOverview.getVisibility() == View.VISIBLE) {
                    hideFolderView(v);
                } else {
                    showFolderView(v);
                }
                break;
            case R.id.tv_choose_count:

                if (selectImageBeanList == null || selectImageBeanList.size() == 0)
                    return;

                //直接裁剪
//                try {
//                    startActivityForResult(imagePickerManager.getCropPictureIntent(context,
//                            selectImageBeanList.get(0).getImagePath()), REQUEST_CODE_CROP_PHOTO);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                startActivity(new Intent(context, PhotoPreviewActivity.class));
                break;
            default:
                hideFolderView(v);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {

            if (rlFolderOverview.getVisibility() == View.VISIBLE) {
                hideFolderView(null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(imagePickerManager.getCurrentPhotoPath());
                Log.e("Camera", "success " + imagePickerManager.getCurrentPhotoPath());
//                RxBus.getDefault().postSticky(new KeyEvent(KeyEvent.PHOTO_CAMERA, imagePickerManager.getCurrentPhotoPath()));
                try {
                    startActivityForResult(imagePickerManager.getCropPictureIntent(context, imagePickerManager.getCurrentPhotoPath()), REQUEST_CODE_CROP_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePickerManager.refreshGallery();
            } else if (requestCode == REQUEST_CODE_CROP_PHOTO) {
                String path = data.getStringExtra(CropActivity.CROP_RESULT);
                Log.e("Crop", path);
                finish();
            }
//            else if (requestCode == REQUEST_CODE_PREVIEW) {
//                if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
//                    // 从拍照预览界面返回，刷新图库
//                    mImageCaptureManager.refreshGallery();
//                }
//
//            }
        }
    }
}