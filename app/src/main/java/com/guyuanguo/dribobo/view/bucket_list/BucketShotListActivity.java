package com.guyuanguo.dribobo.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.guyuanguo.dribobo.view.base.SingleFragmentActivity;
import com.guyuanguo.dribobo.view.shot_list.ShotFragment;

/**
 * Created by guyuanguo on 9/27/16.
 */
public class BucketShotListActivity extends SingleFragmentActivity {

    public static final String KEY_BUCKET_NAME = "bucketName";
    public static final String KEY_BUCKET_DESCRIPTION = "bucketDescription";
    @NonNull
    @Override
    protected Fragment newFragment() {
        String bucketId = getIntent().getStringExtra(ShotFragment.KEY_BUCKET_ID);
        String name = getIntent().getStringExtra(KEY_BUCKET_NAME);
        String description = getIntent().getStringExtra(KEY_BUCKET_DESCRIPTION);
        return bucketId == null
                ? ShotFragment.newInstance(ShotFragment.LIST_TYPE_HOME)
                : ShotFragment.newBucketListInstance(bucketId, name, description);

    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }
}
