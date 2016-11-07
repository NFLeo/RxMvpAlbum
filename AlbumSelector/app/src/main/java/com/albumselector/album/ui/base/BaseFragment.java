package com.albumselector.album.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.albumselector.album.entity.Configuration;
import com.albumselector.album.ui.PhotoActivity;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午10:46
 */
public abstract class BaseFragment extends Fragment {

    public Context context;
    public PhotoActivity photoActivity;
    private final String CLASS_NAME = getClass().getSimpleName();
    public static final String EXTRA_PREFIX = "leo.album";
    public static final String EXTRA_CONFIGURATION = EXTRA_PREFIX +".Configuration";

    protected Bundle mSaveDataBundle;
    protected String BUNDLE_KEY = "KEY_" + CLASS_NAME;

    protected Configuration mConfiguration;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        Bundle argsBundle = getArguments();

        if(savedInstanceState != null){
            mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION);
        }
        if(mConfiguration == null && argsBundle != null) {
            mConfiguration = argsBundle.getParcelable(EXTRA_CONFIGURATION);
        }

        if(mConfiguration != null){
            if(argsBundle == null){
                argsBundle = savedInstanceState;
            }
            findView(view);
            onViewCreatedOk(view, argsBundle);
        } else {
            if(getActivity() != null && !getActivity().isFinishing()) {
                getActivity().finish();
            }
        }
    }

    protected abstract void findView(View view);

    public abstract void onViewCreatedOk(View view, @Nullable Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof PhotoActivity) {
            photoActivity = (PhotoActivity) activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public abstract int getContentView();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched();
        }
    }

    protected abstract void onFirstTimeLaunched();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null) {
            mSaveDataBundle = saveState();
        }

        if (mSaveDataBundle != null) {
            Bundle b = getArguments();
            if(b != null) {
                b.putBundle(BUNDLE_KEY, mSaveDataBundle);
            }
        }
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if(b != null) {
            mSaveDataBundle = b.getBundle(BUNDLE_KEY);
            if (mSaveDataBundle != null) {
                restoreState();
                return true;
            }
        }
        return false;
    }

    /**
     * Restore Instance State Here
     */
    private void restoreState() {
        if (mSaveDataBundle != null) {
            mConfiguration = mSaveDataBundle.getParcelable(EXTRA_CONFIGURATION);
            onRestoreState(mSaveDataBundle);
        }
    }

    /**
     * 恢复数据
     * @param savedInstanceState
     */
    protected abstract void onRestoreState(Bundle savedInstanceState);

    /**
     * Save Instance State Here
     */
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putParcelable(EXTRA_CONFIGURATION, mConfiguration);
        onSaveState(state);
        return state;
    }

    protected abstract void onSaveState(Bundle outState);
}
