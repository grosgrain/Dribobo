package com.guyuanguo.dribobo.view.shot_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.guyuanguo.dribobo.Dribbble.Dribbble;
import com.guyuanguo.dribobo.Dribbble.DribbbleException;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Bucket;
import com.guyuanguo.dribobo.model.Shot;
import com.guyuanguo.dribobo.utils.ModelUtils;
import com.guyuanguo.dribobo.view.base.DribbbleTask;
import com.guyuanguo.dribobo.view.bucket_list.BucketFragment;
import com.guyuanguo.dribobo.view.bucket_list.ChooseBucketActivity;
import com.guyuanguo.dribobo.view.shot_list.ShotAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guyuanguo on 9/19/16.
 */
public class ShotDetailFragment extends Fragment {

    public static final String KEY_SHOT = "shot";
    public static final int REQ_CODE_BUCKET = 100;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout_shot) SwipeRefreshLayout swipeRefreshLayout;

    private  ShotDetailAdapter adapter;
    private  Shot shot;
    private boolean isLiking;
    private ArrayList<String> collectedBucketIds;

    public static ShotDetailFragment newInstance(@NonNull Bundle args) {
        ShotDetailFragment fragment = new ShotDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout.setEnabled(false);
        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>(){});
        adapter = new ShotDetailAdapter(this, shot);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        AsyncTaskCompat.executeParallel(new LoadBucketsTask());
        isLiking = true;
        AsyncTaskCompat.executeParallel(new CheckLikeTask());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBucketIds = data.getStringArrayListExtra(
                    BucketFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addedBucketIds = new ArrayList<>();
            List<String> removedBucketIds = new ArrayList<>();
            for (String chosenBucketId : chosenBucketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addedBucketIds.add(chosenBucketId);
                }
            }

            for (String collectedBucketId : collectedBucketIds) {
                if (!chosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }

            AsyncTaskCompat.executeParallel(new UpdateBucketTask(addedBucketIds, removedBucketIds));
        }
    }

    public void like(@NonNull String shotId, boolean like) {
        if(!isLiking) {
            isLiking = true;
            AsyncTaskCompat.executeParallel(new LikeTask(shotId, like));
        }
    }

    public void bucket() {
        if (collectedBucketIds == null) {
            Snackbar.make(getView(), R.string.shot_detail_loading_buckets, Snackbar.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), ChooseBucketActivity.class);
            intent.putExtra(BucketFragment.KEY_CHOOSING_MODE, true);
            intent.putStringArrayListExtra(BucketFragment.KEY_COLLECTED_BUCKET_IDS,
                    collectedBucketIds);
            startActivityForResult(intent, REQ_CODE_BUCKET);
        }
    }


    private class LoadBucketsTask extends DribbbleTask<Void, Void, List<String>> {

        @Override
        protected List<String> doJob(Void... params) throws DribbbleException {
            List<Bucket> shotBuckets = Dribbble.getShotBuckets(shot.id);
            List<Bucket> userBuckets = Dribbble.getUserBuckets();

            Set<String> userBucketIds = new HashSet<>();
            for (Bucket userBucket : userBuckets) {
                userBucketIds.add(userBucket.id);
            }

            List<String> collectedBucketIds = new ArrayList<>();
            for (Bucket shotBucket : shotBuckets) {
                if (userBucketIds.contains(shotBucket.id)) {
                    collectedBucketIds.add(shotBucket.id);
                }
            }

            return collectedBucketIds;
        }

        @Override
        protected void onSuccess(List<String> result) {
            collectedBucketIds = new ArrayList<>(result);

            if (result.size() > 0) {
                shot.bucketed = true;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class UpdateBucketTask extends DribbbleTask<Void, Void, Void> {

        private List<String> added;
        private List<String> removed;

        private UpdateBucketTask(@NonNull List<String> added,
                                 @NonNull List<String> removed) {
            this.added = added;
            this.removed = removed;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            for (String addedId : added) {
                Dribbble.addBucketShot(addedId, shot.id);
            }

            for (String removedId : removed) {
                Dribbble.removeBucketShot(removedId, shot.id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            collectedBucketIds.addAll(added);
            collectedBucketIds.removeAll(removed);

            shot.bucketed = !collectedBucketIds.isEmpty();
            shot.buckets_count += added.size() - removed.size();

            recyclerView.getAdapter().notifyDataSetChanged();

            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>(){}));
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    private class CheckLikeTask extends DribbbleTask<Void, Void, Boolean> {

        @Override
        protected Boolean doJob(Void... params) throws DribbbleException {
            return Dribbble.isLikingShot(shot.id);
        }

        @Override
        protected void onSuccess(Boolean result) {
            isLiking = false;
            shot.liked = result;
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class LikeTask extends DribbbleTask<Void, Void, Void> {

        private String id;
        private boolean like;

        public LikeTask(String id, boolean like) {
            this.id = id;
            this.like = like;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            if (like) {
                Dribbble.likeShot(id);
            } else {
                Dribbble.unlikeShot(id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void s) {
            isLiking = false;

            shot.liked = like;
            shot.likes_count += like ? 1 : -1;
            recyclerView.getAdapter().notifyDataSetChanged();

            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shot.title + " " + shot.html_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_shot)));
    }

}
