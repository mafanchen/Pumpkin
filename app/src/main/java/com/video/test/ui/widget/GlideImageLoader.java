package com.video.test.ui.widget;

import android.content.Context;
import android.widget.ImageView;

import com.video.test.framework.GlideApp;
import com.youth.banner.loader.ImageLoader;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Enoch on 2017/5/25.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

        GlideApp.with(context).load(path).transition(withCrossFade()).into(imageView);

    }
}
