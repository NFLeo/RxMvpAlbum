package com.albumselector.album.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.albumselector.album.entity.Configuration;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/16 下午7:36
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_PREFIX = "leo.album";
    public static final String EXTRA_CONFIGURATION = EXTRA_PREFIX +".Configuration";

    private final String CLASS_NAME = getClass().getSimpleName();

    public Configuration mConfiguration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = null;
        if(intent != null){
            bundle = intent.getExtras();
        }

        if(savedInstanceState != null){
            mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION);
        }
        if(mConfiguration == null && bundle != null) {
            mConfiguration = bundle.getParcelable(EXTRA_CONFIGURATION);
        }

        if(mConfiguration == null){
            mFinishHanlder.sendEmptyMessage(0);
        } else {
            if(bundle == null){
                bundle = savedInstanceState;
            }
            setContentView(getContentView());
            initPrepare();
            findViews();
            onCreateOk(bundle);
        }
    }

    @LayoutRes
    public abstract int getContentView();

    protected abstract void onCreateOk(@Nullable Bundle savedInstanceState);

    protected abstract void initPrepare();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CONFIGURATION, mConfiguration);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION);
    }

    public abstract void findViews();

    protected Handler mFinishHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };
}
