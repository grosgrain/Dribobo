package com.guyuanguo.dribobo.view.shot_detail;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.guyuanguo.dribobo.view.base.SingleFragmentActivity;

public class ShotDetailActivity extends SingleFragmentActivity {

    public static final String KEY_SHOT_TITLE = "shot_title";

    @NonNull
    @Override
    protected Fragment newFragment() {
        return  ShotDetailFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
