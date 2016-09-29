package com.guyuanguo.dribobo.view.shot_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by guyuanguo on 9/14/16.
 */
public class ShotViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_clickable_cover) View cover;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_image) SimpleDraweeView image;
    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
