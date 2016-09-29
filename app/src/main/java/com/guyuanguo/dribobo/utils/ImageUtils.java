package com.guyuanguo.dribobo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.model.Shot;

public class ImageUtils {

    public static void loadShotImage(@NonNull Shot shot, @NonNull SimpleDraweeView imageView) {
        String imageUrl = shot.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Uri imageUri = Uri.parse(imageUrl);
            if (shot.animated) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                                    .setUri(imageUri)
                                                    .setAutoPlayAnimations(true)
                                                    .build();
                imageView.setController(controller);
            } else {
                imageView.setImageURI(imageUri);
            }
        }
    }
}
