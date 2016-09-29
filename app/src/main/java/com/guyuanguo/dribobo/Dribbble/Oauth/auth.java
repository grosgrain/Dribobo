package com.guyuanguo.dribobo.Dribbble.Oauth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class auth {
    public static final int REQ_CODE = 100;
    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String CLIENT_ID = "877c4c54eff53b636ab4a8286ddf7fedbe0bf88fce5fba5cb64c49c7f6abad22";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    public static final String REDIRECT_URI = "http://www.google.com";
    private static final String KEY_SCOPE = "scope";
    private static final String SCOPE = "public+write";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String CLIENT_SECRET = "82fce1b9607739c1b83b5c161d4ba866cd0ab9394abc00fb4480fa6672a68863";
    private static final String KEY_CODE = "code";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public static String getAuthorizeUrl() {
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();
        // fix encode issue
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        return url;
    }

    public static void openAuthActivity(@NonNull Activity activity) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra(WebviewActivity.KEY_URL, getAuthorizeUrl());
        activity.startActivityForResult(intent, REQ_CODE);
    }

    public static String fetchAccessToken(String authCode)
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, authCode)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();
        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();
        Response response = client.newCall(request).execute();

        String responseString = response.body().string();
        try {
            JSONObject obj = new JSONObject(responseString);
            return obj.getString(KEY_ACCESS_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}

