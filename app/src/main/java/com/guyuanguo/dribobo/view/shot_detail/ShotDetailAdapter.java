package com.guyuanguo.dribobo.view.shot_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Shot;
import com.guyuanguo.dribobo.view.bucket_list.BucketFragment;
import com.guyuanguo.dribobo.view.bucket_list.ChooseBucketActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by guyuanguo on 9/19/16.
 */
public class ShotDetailAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_INFO = 1;

    private Shot shot;
    private final ShotDetailFragment shotDetailFragment;


    public ShotDetailAdapter(@NonNull ShotDetailFragment shotDetailFragment, @NonNull Shot shot) {
        this.shotDetailFragment = shotDetailFragment;
        this.shot = shot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item_image,parent,false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item_info,parent,false);
                return new InfoViewHolder(view);
            default:
                return null;
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final  int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                // play gif automatically
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(shot.getImageUrl()))
                        .setAutoPlayAnimations(true)
                        .build();
                ((ImageViewHolder) holder).image.setController(controller);
                break;
            case VIEW_TYPE_SHOT_INFO:
                InfoViewHolder shotInfoVIewHolder = (InfoViewHolder) holder;
                shotInfoVIewHolder.title.setText(shot.title);
                shotInfoVIewHolder.authorName.setText(shot.user.name);
                // remove html tag except <a></a> from description
                if (shot.description == null || shot.description.length() == 0) {
                    shotInfoVIewHolder.description.setText("");
                } else {
                    shotInfoVIewHolder.description.setText(Html.fromHtml(shot.description));
                    shotInfoVIewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());
                }
                shotInfoVIewHolder.authorPicture.setImageURI(Uri.parse(shot.user.avatar_url));
                shotInfoVIewHolder.view.setText(String.valueOf(shot.views_count));
                shotInfoVIewHolder.like.setText(String.valueOf(shot.likes_count));
                if (shot.liked) {
                    Drawable img = ContextCompat.getDrawable(shotInfoVIewHolder.itemView.getContext(),R.drawable.ic_favorite_red_100_18dp);
                    shotInfoVIewHolder.like.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    shotInfoVIewHolder.like.setTextColor(shotInfoVIewHolder.itemView.getResources().getColor(R.color.colorPrimary));
                } else {
                    Drawable img = ContextCompat.getDrawable(shotInfoVIewHolder.itemView.getContext(),R.drawable.ic_favorite_grey_600_18dp);
                    shotInfoVIewHolder.like.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    shotInfoVIewHolder.like.setTextColor(shotInfoVIewHolder.itemView.getResources().getColor(R.color.colorAccent));

                }
                shotInfoVIewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.like(shot.id, !shot.liked);
                    }
                });
                shotInfoVIewHolder.bucket.setText(String.valueOf(shot.buckets_count));
                if (shot.bucketed) {
                    Drawable img = ContextCompat.getDrawable(shotInfoVIewHolder.itemView.getContext(),R.drawable.ic_inbox_red_100_18dp);
                    shotInfoVIewHolder.bucket.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    shotInfoVIewHolder.bucket.setTextColor(shotInfoVIewHolder.itemView.getResources().getColor(R.color.colorPrimary));

                } else {
                    Drawable img = ContextCompat.getDrawable(shotInfoVIewHolder.itemView.getContext(),R.drawable.ic_inbox_grey_600_18dp);
                    shotInfoVIewHolder.bucket.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    shotInfoVIewHolder.bucket.setTextColor(shotInfoVIewHolder.itemView.getResources().getColor(R.color.colorAccent));

                }
                shotInfoVIewHolder.bucket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.bucket();
                    }
                });
                shotInfoVIewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotDetailFragment.share();
                    }
                });
                break;
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_SHOT_IMAGE : VIEW_TYPE_SHOT_INFO;
    }
}
