package com.guyuanguo.dribobo.view.bucket_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.shot_list.ShotFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guyuanguo on 9/22/16.
 */
public class NewBucketDialogFragment extends DialogFragment {
    public static final String KEY_BUCKET_NAME = "bucket_name";
    public static final String KEY_BUCKET_DESCRIPTION = "bucket_description";
    public static final String TAG = "NewBucketDialogFragment";

    @BindView(R.id.new_bucket_name) EditText bucketName;
    @BindView(R.id.new_bucket_description) EditText bucketDescription;

    private boolean isEditing;
    private String name;
    private String description;


    public NewBucketDialogFragment() {
        this.isEditing = false;
    }

    @SuppressLint("ValidFragment")
    public NewBucketDialogFragment(String name , String description) {
        this.name = name;
        this.description = description;
        this.isEditing = true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_bucket, null);
        ButterKnife.bind(this,view);
        if (isEditing) {
            bucketName.setText(name);
            bucketDescription.setText(description);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AboutDialog))
                .setView(view).setTitle(isEditing
                        ? R.string.edit_bucket_title :
                          R.string.new_bucket_title)
                .setPositiveButton(isEditing
                        ? R.string.edit_bucekt_edit
                        : R.string.new_bucekt_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent result = new Intent();
                        result.putExtra(KEY_BUCKET_NAME, bucketName.getText().toString());
                        result.putExtra(KEY_BUCKET_DESCRIPTION, bucketDescription.getText().toString());
                        if (isEditing) {
                            getTargetFragment().onActivityResult(ShotFragment.REQ_CODE_EDIT,
                                    Activity.RESULT_OK,
                                    result);
                        } else {
                            getTargetFragment().onActivityResult(BucketFragment.REQ_CODE_NEW_BUCKET,
                                    Activity.RESULT_OK,
                                    result);
                        }
                        dismiss();
                    }
                }).setNegativeButton(R.string.new_bucket_cancel,null);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setTextColor(getResources().getColor(R.color.colorPrimary));
        return dialog;
    }
}
