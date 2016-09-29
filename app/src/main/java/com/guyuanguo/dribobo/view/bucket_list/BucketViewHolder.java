package com.guyuanguo.dribobo.view.bucket_list;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by guyuanguo on 9/22/16.
 */
public class BucketViewHolder extends BaseViewHolder {
    @BindView(R.id.bucket_relative) RelativeLayout bucket_relative;
    @BindView(R.id.bucket_layout) View bucketLayout;
    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(R.id.bucket_shot_chosen) ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
