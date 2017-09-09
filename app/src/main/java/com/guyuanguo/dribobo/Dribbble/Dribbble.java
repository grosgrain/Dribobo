package com.guyuanguo.dribobo.Dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.guyuanguo.dribobo.model.Bucket;
import com.guyuanguo.dribobo.model.Like;
import com.guyuanguo.dribobo.model.Shot;
import com.guyuanguo.dribobo.model.User;
import com.guyuanguo.dribobo.utils.ModelUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Fetch data from dribbble API
 */
public class Dribbble {
    public static final int COUNT_PER_LOAD = 12;
    private static String accessToken;
    private static User user;
    public static final String SP_AUTH = "auth";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String TAG = "Dribbble API";
    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String USER_END_POINT = API_URL + "user";
    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final String SHOT_END_POINT = API_URL + "shots";
    private static final TypeToken<List<Shot>> SHOT_TYPE = new TypeToken<List<Shot>>(){};
    private static final String BUCKETS_END_POINT = API_URL + "buckets";
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static OkHttpClient client = new OkHttpClient();
    private static final String KEY_USER = "user";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final String KEY_SHOT_ID = "shot_id";
    private static final String KEY_BUCKET_ID = "bucket_id";
    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};

    public static boolean isLoggedIn() {
        return accessToken != null && user != null;
    }

    public static void login(@NonNull Context context, @NonNull String accessToken) throws DribbbleException{
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);
        Dribbble.user = getUser();
        storeUser(context, user);
    }

    private static void storeAccessToken(@NonNull Context context, @Nullable String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    private static User getUser() throws DribbbleException{
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    private static <T> T parseResponse(Response response,
                                       TypeToken<T> typeToken) throws DribbbleException {
        String responseString;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }

        Log.d(TAG, responseString);

        try {
            return ModelUtils.toObject(responseString, typeToken);
        } catch (JsonSyntaxException e) {
            throw new DribbbleException(responseString);
        }
    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws DribbbleException {
        if (response.code() != statusCode) {
            throw new DribbbleException(response.message());
        }
    }

    private static Response makeGetRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url).build();
        return makeRequest(request);
    }

    private static Response makePostRequest(String url,
                                            RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url,
                                           RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .put(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url,
                                              RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete()
                .build();
        return makeRequest(request);
    }

    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeRequest(Request request) throws DribbbleException {
        try {
            Response response = client.newCall(request).execute();
//            Log.d(TAG, response.header("X-RateLimit-Remaining"));
            return response;
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }
    }

    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static List<Shot> getShot(int page) throws DribbbleException {
        String url = SHOT_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    /**
     * @return All the buckets for the logged in user
     * @throws DribbbleException
     */
    public static List<Bucket> getUserBuckets() throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets(int page) throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets(@NonNull String userId,
                                              int page) throws DribbbleException {
        String url = USER_END_POINT + "/" + userId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * @param shotId
     * @return All the buckets which a certain shot has been put into
     * @throws DribbbleException
     */
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws DribbbleException {
        String url = SHOT_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket newBucket(@NonNull String name,
                                   @Nullable String description) throws DribbbleException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody),BUCKET_TYPE) ;
    }

    public static void deleteBucket(@NonNull String bucketId) throws DribbbleException{
        String url = BUCKETS_END_POINT + "/" + bucketId;
        FormBody formBody = new FormBody.Builder().add(KEY_BUCKET_ID, bucketId).build();
        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void updateBucket(@NonNull String bucketId, @NonNull String name,
                                    @Nullable String description) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId;
        FormBody formBody = new FormBody.Builder()
                .add(KEY_BUCKET_ID, bucketId)
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    /**
     * Add a shot to a bucket
     * @param bucketId
     * @param shotId
     * @throws DribbbleException
     * @throws JsonSyntaxException
     */
    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    /**
     * Remove a shot from a bucket
     * @param bucketId
     * @param shotId
     * @throws DribbbleException
     * @throws JsonSyntaxException
     */
    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public static List<Like> getLikes(int page) throws DribbbleException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }
    
    public static List<Shot> getLikedShots(int page) throws DribbbleException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<Shot>();
        for(Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }

    public static boolean isLikingShot(@NonNull String id) throws DribbbleException {
        String url = SHOT_END_POINT + "/" + id + "/like";
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }

    public static Like likeShot(@NonNull String id) throws DribbbleException {
        String url = SHOT_END_POINT + "/" + id + "/like";
        Response response = makePostRequest(url, new FormBody.Builder().build());

        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);

        return parseResponse(response, LIKE_TYPE);
    }

    public static void unlikeShot(@NonNull String id) throws DribbbleException {
        String url = SHOT_END_POINT + "/" + id + "/like";
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static List<Shot> getBucketShots(@NonNull String bucketId,
                                            int page) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    public static User getCurrentUser() {
        return user;
    }

    public static void logout(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);
        accessToken = null;
        user = null;
    }
}
