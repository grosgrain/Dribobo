package com.guyuanguo.dribobo.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

public class Shot {

    public static final String IMAGE_HIDPI = "hidpi";
    public static final String IMAGE_NORMAL = "normal";


    public String id;
    public String title;
    public String description;

    public int views_count;
    public int likes_count;
    public int buckets_count;
    public String html_url;
    public Map<String, String> images;

    public Date created_at;
    public boolean animated;
    public boolean liked;
    public boolean bucketed;
    public User user;

    @NonNull
    public String getImageUrl() {
        if (images == null) {
            return "";
        }

        String url = images.containsKey(IMAGE_HIDPI)
                ? images.get(IMAGE_HIDPI)
                : images.get(IMAGE_NORMAL);
        return url == null ? "" : url;
    }
}
