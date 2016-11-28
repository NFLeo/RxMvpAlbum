package com.albumselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.albumselector.album.baserx.RxBus;
import com.albumselector.album.entity.Configuration;
import com.albumselector.album.rxbus.event.KeyEvent;
import com.albumselector.album.ui.PhotoPickerActivity;
import com.albumselector.album.ui.mvp.BaseMvpActivity;
import com.albumselector.album.ui.mvp.BasePresenter;
import com.bumptech.glide.Glide;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends BaseMvpActivity {

    @Override
    protected BasePresenter createPresenterInstance() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewCreated() {
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        TextView textView1 = (TextView) findViewById(R.id.text1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                startActivity(intent);
            }
        });

        final ImageView imageView = (ImageView) findViewById(R.id.image);

        rxBusManager.add(RxBus.getDefault().toObservable(KeyEvent.class)
                .flatMap(new Func1<KeyEvent, Observable<String>>() {
                    @Override
                    public Observable<String> call(KeyEvent keyEvent) {
                        String path = "";

                        if (keyEvent.getKey() == KeyEvent.PHOTO_CAMERA_CROP && keyEvent.getValue() != null)
                            path = (String) keyEvent.getValue();

                        Log.e("Crop success", path);
                        return Observable.just(path);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Glide.with(MainActivity.this).load(s).asBitmap().into(imageView);
                    }
                }));
    }
}
