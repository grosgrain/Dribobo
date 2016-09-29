package com.guyuanguo.dribobo.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.base.SingleFragmentActivity;

import java.util.ArrayList;

/**
 * Created by guyuanguo on 9/23/16.
 */
public class ChooseBucketActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment newFragment() {
        boolean isChoosingMode = getIntent().getExtras().getBoolean(
                BucketFragment.KEY_CHOOSING_MODE);
        ArrayList<String> chosenBucketIds = getIntent().getExtras().getStringArrayList(
                BucketFragment.KEY_COLLECTED_BUCKET_IDS);
        return BucketFragment.newInstance(null, isChoosingMode, chosenBucketIds);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
