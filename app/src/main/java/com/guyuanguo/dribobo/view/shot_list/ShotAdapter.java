package com.guyuanguo.dribobo.view.shot_list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.gson.reflect.TypeToken;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Shot;
import com.guyuanguo.dribobo.utils.ImageUtils;
import com.guyuanguo.dribobo.utils.ModelUtils;
import com.guyuanguo.dribobo.view.base.BaseViewHolder;
import com.guyuanguo.dribobo.view.base.InfiniteAdapter;
import com.guyuanguo.dribobo.view.shot_detail.ShotDetailActivity;
import com.guyuanguo.dribobo.view.shot_detail.ShotDetailAdapter;
import com.guyuanguo.dribobo.view.shot_detail.ShotDetailFragment;

import java.util.List;

/**
 * Created by guyuanguo on 9/14/16.
 */
public class ShotAdapter extends InfiniteAdapter<Shot> {

    private final ShotFragment shotFragment;

    public ShotAdapter(@NonNull ShotFragment shotFragment, @NonNull List<Shot> data, @NonNull LoadMoreListener loadMoreListener) {
        super(shotFragment.getContext(), data, loadMoreListener);
        this.shotFragment = shotFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_shot, parent, false);
        return new ShotViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
        final Shot shot = getData().get(position);
        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShotDetailActivity.class);
                intent.putExtra(ShotDetailFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                intent.putExtra(ShotDetailActivity.KEY_SHOT_TITLE, shot.title);
                shotFragment.startActivityForResult(intent, ShotFragment.REQ_CODE_SHOT);
            }
        });
        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
        ImageUtils.loadShotImage(shot, shotViewHolder.image);
    }
}
