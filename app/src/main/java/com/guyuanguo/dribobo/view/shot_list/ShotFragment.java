package com.guyuanguo.dribobo.view.shot_list;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.guyuanguo.dribobo.Dribbble.Dribbble;
import com.guyuanguo.dribobo.Dribbble.DribbbleException;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Bucket;
import com.guyuanguo.dribobo.model.Shot;
import com.guyuanguo.dribobo.utils.ModelUtils;
import com.guyuanguo.dribobo.view.base.DribbbleTask;
import com.guyuanguo.dribobo.view.base.InfiniteAdapter;
import com.guyuanguo.dribobo.view.base.SpaceItemDecoration;
import com.guyuanguo.dribobo.view.bucket_list.BucketShotListActivity;
import com.guyuanguo.dribobo.view.bucket_list.NewBucketDialogFragment;
import com.guyuanguo.dribobo.view.shot_detail.ShotDetailFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guyuanguo on 9/14/16.
 */
public class ShotFragment extends Fragment {
    @BindView(R.id.recyclerView) RecyclerView recyclerView_shot;
    @BindView(R.id.swipe_refresh_layout_shot) SwipeRefreshLayout swipeRefreshLayout;
    public ShotAdapter adapter;

    public static final int REQ_CODE_SHOT = 100;
    public static final int REQ_CODE_EDIT = 101;
    public static final String KEY_DELETE_BUCKET_ID = "delete_bucket_id";

    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final String KEY_BUCKET_NAME = "name";
    public static final String KEY_BUCKET_DESCRIPTION = "description";
    public static final int LIST_TYPE_HOME = 1;
    public static final int LIST_TYPE_LIKED= 2;
    public static final int LIST_TYPE_BUCKET= 3;

    public String name;
    public String description;

    private int listType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadShotsTask(false));
            }
        }
    };

    public static ShotFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShotFragment newBucketListInstance(@NonNull String bucketId,
                                                     @Nullable String name,
                                                     @Nullable String description) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);
        args.putString(KEY_BUCKET_NAME, name);
        args.putString(KEY_BUCKET_DESCRIPTION,description);
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SHOT:
                    Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotDetailFragment.KEY_SHOT),
                            new TypeToken<Shot>(){});
                    for (Shot shot : adapter.getData()) {
                        if (TextUtils.equals(shot.id, updatedShot.id)) {
                            shot.likes_count = updatedShot.likes_count;
                            shot.buckets_count = updatedShot.buckets_count;
                            adapter.notifyDataSetChanged();
                            return;
                        }
                    }
                    break;
                case REQ_CODE_EDIT:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
                    String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
                    this.name = bucketName;
                    this.description = bucketDescription;
                    getActivity().setTitle(bucketName);
                    if (!TextUtils.isEmpty(bucketId)) {
                        AsyncTaskCompat.executeParallel(new UpdateBucketTask(bucketId, bucketName, bucketDescription));
                    }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.name = getArguments().getString(KEY_BUCKET_NAME);
        this.description = getArguments().getString(KEY_BUCKET_DESCRIPTION);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
            }
        });
        listType = getArguments().getInt(KEY_LIST_TYPE);
        recyclerView_shot.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_shot.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        adapter = new ShotAdapter(this,new ArrayList<Shot>(), onLoadMore);
        recyclerView_shot.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listType == LIST_TYPE_BUCKET) {
            inflater.inflate(R.menu.bucket_list_delete_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                NewBucketDialogFragment dialogFragment = new NewBucketDialogFragment(name,description);
                dialogFragment.setTargetFragment(ShotFragment.this, REQ_CODE_EDIT);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
                break;
            case R.id.delete:
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Bucket")
                        .setMessage("Are you sure you want to delete this bucket?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                String bucketId = getArguments().getString(KEY_BUCKET_ID);
                                Intent intent = new Intent();
                                intent.putExtra(KEY_DELETE_BUCKET_ID, bucketId);
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Shot>> {

        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            switch (listType) {
                case LIST_TYPE_HOME:
                    return Dribbble.getShot(page);
                case LIST_TYPE_LIKED:
                    List<Shot> res = Dribbble.getLikedShots(page);
                    if (res.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(getView(), "You have not liked any shot. Like one now :)", Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();
                        snackbarView .setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                    return res;
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return Dribbble.getBucketShots(bucketId, page);
                default:
                    return Dribbble.getShot(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= Dribbble.COUNT_PER_LOAD);
            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class UpdateBucketTask extends DribbbleTask<Void, Void, Void> {

        private String id;
        private String name;
        private String description;

        private UpdateBucketTask(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            Dribbble.updateBucket(id, name, description);
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            // do nothing
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
