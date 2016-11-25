package com.albumselector.album.widget.cropimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.albumselector.R;
import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.rxbus.event.KeyEvent;
import com.albumselector.album.utils.ActivityUtils;

/**
 * Created by lijunguan on 2016/4/26.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class CropActivity extends AppCompatActivity implements CropFragment.CropImageListener
{
    private CropFragment mCropFragment;
    private TextView btnSubmit;

    public static final String CROP_RESULT = "cropResult";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (savedInstanceState == null) {
            String iamgePath = getIntent().getStringExtra(CropFragment.ARG_IMAGE_PATH);
            mCropFragment = CropFragment.newInstance(iamgePath);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    mCropFragment,
                    CropFragment.TAG,
                    false
            );
        }

        btnSubmit = (TextView) findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCropFragment != null)
                    mCropFragment.cropImage();
            }
        });
    }

    @Override
    public void onCropCompleted(String path) {

        RxBus.getDefault().post(new KeyEvent(KeyEvent.PHOTO_CAMERA_CROP, path));

        Intent intent = new Intent();
        intent.putExtra(CROP_RESULT, path);
        setResult(RESULT_OK, intent);
        finish();
    }
}
