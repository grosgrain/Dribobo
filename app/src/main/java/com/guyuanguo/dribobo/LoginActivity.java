package com.guyuanguo.dribobo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.google.gson.JsonSyntaxException;
import com.guyuanguo.dribobo.Dribbble.Dribbble;
import com.guyuanguo.dribobo.Dribbble.DribbbleException;
import com.guyuanguo.dribobo.Dribbble.Oauth.WebviewActivity;
import com.guyuanguo.dribobo.Dribbble.Oauth.auth;
import com.guyuanguo.dribobo.view.MainActivity;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    public static final int REQ_CODE_LOGIN = 100;
    @BindView(R.id.login_area) LinearLayout login_area;
    @BindView(R.id.login_logo) ImageView login_logo;
    @BindView(R.id.login_text) TextView login_text;
    @BindView(R.id.loginActivity) ImageView loginActivity;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
                    loginActivity.setImageBitmap(background);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        // Set login_text font
        AssetManager mgr=getAssets();
        Typeface tf= Typeface.createFromAsset(mgr, "fonts/Anjelika-Custome.ttf");
        login_text.setTypeface(tf);
        login_text.setGravity(Gravity.CENTER);
        // load access token from shared preference
        Dribbble.init(this);
        if (!Dribbble.isLoggedIn()) {
            login_area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.openAuthActivity(LoginActivity.this);
                }
            });
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == auth.REQ_CODE && resultCode == Activity.RESULT_OK) {
            final String authCode = data.getStringExtra(WebviewActivity.KEY_CODE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = auth.fetchAccessToken(authCode);
                        Dribbble.login(LoginActivity.this, token);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException | DribbbleException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
