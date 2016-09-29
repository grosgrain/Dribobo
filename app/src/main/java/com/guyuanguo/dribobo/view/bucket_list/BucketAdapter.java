package com.guyuanguo.dribobo.view.bucket_list;

import android.app.Activity;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Bucket;
import com.guyuanguo.dribobo.view.base.BaseViewHolder;
import com.guyuanguo.dribobo.view.base.InfiniteAdapter;
import com.guyuanguo.dribobo.view.shot_list.ShotFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by guyuanguo on 9/22/16.
 */
public class BucketAdapter extends InfiniteAdapter<Bucket> {
    @BindColor(R.color.colorPrimary) int colorPrimary;
    @BindColor(R.color.colorAccent) int colorAccent;

    private boolean isChoosingMode;
    private BucketFragment bucketFragment;
    private List<Bucket> data;

    public BucketAdapter(@NonNull BucketFragment bucketFragment,
                             @NonNull List<Bucket> data,
                             @NonNull LoadMoreListener loadMoreListener,
                             boolean isChoosingMode) {
        super(bucketFragment.getContext(), data, loadMoreListener);
        this.bucketFragment = bucketFragment;
        this.data = data;
        this.isChoosingMode = isChoosingMode;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        ButterKnife.bind(this,view);
        return new BucketViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);
        final BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(formatShotCount(bucket.shots_count));
        Context context = holder.itemView.getContext();
        final int colorTitle = Color.parseColor("#cc000000");
        if (isChoosingMode) {
            bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);
            bucketViewHolder.bucketChosen.setImageDrawable(
                    bucket.isChoosing ?
                            ContextCompat.getDrawable(context, R.drawable.ic_check_box_white_24dp) :
                            ContextCompat.getDrawable(context, R.drawable.ic_check_box_outline_blank_grey_600_24dp)
            );
            // Set backgound and text color
            ColorDrawable drawable = (ColorDrawable) bucketViewHolder.bucket_relative.getBackground();
            final int old_background = drawable.getColor();
            final int colorWhite = Color.WHITE;

            bucketViewHolder.bucket_relative.setBackgroundColor(bucket.isChoosing ?
                        colorPrimary : old_background);
            bucketViewHolder.bucketName.setTextColor(bucket.isChoosing ?
                        colorWhite : colorTitle);
            bucketViewHolder.bucketShotCount.setTextColor(bucket.isChoosing ?
            colorWhite : colorAccent);

            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bucket.isChoosing = !bucket.isChoosing;
                    notifyItemChanged(position);
                }
            });
        } else {
            bucketViewHolder.bucketName.setTextColor(colorTitle);
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), BucketShotListActivity.class);
                    intent.putExtra(ShotFragment.KEY_BUCKET_ID, bucket.id);
                    intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.name);
                    intent.putExtra(BucketShotListActivity.KEY_BUCKET_DESCRIPTION, bucket.description);
                    bucketFragment.startActivityForResult(intent, BucketFragment.REQ_CODE_BUCKET_CHANGE);
                }
            });
        }
    }

    private String formatShotCount(int shotCount) {
        return shotCount == 0
                ? getContext().getString(R.string.shot_count_single, shotCount)
                : getContext().getString(R.string.shot_count_plural, shotCount);
    }

    public void delete(String bucketId) {
        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i).id.equals(bucketId)) {
                data.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
