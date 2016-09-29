package com.guyuanguo.dribobo.view.shot_detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by guyuanguo on 9/19/16.
 */
public class InfoViewHolder extends BaseViewHolder {
    @BindView(R.id.shot_title) TextView title;
    @BindView(R.id.shot_description) TextView description;
    @BindView(R.id.shot_author_picture) SimpleDraweeView authorPicture;
    @BindView(R.id.shot_author_name) TextView authorName;
    @BindView(R.id.view) TextView view;
    @BindView(R.id.like) TextView like;
    @BindView(R.id.bucket) TextView bucket;
    @BindView(R.id.share) TextView share;

    public InfoViewHolder(View itemView) {
        super(itemView);
    }
}
