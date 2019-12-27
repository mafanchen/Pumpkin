package com.video.test.module.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.utils.GeneratePictureUtils;


/**
 * Created by HomgWu on 2017/11/29.
 */

public class GenerateModel {

    protected Context mContext;
    private ViewGroup mRootView;
    private Bitmap mCodeBitmap;
    private GeneratePictureUtils mGeneratePictureManager;
    private View mShareView;
    private String mSavePath;
    private String mWebUrl;
    private String mShareBg;

    public GenerateModel(ViewGroup rootView, Bitmap codeBitmap, String webUrl, String shareBg) {
        this.mContext = rootView.getContext();
        this.mRootView = rootView;
        this.mCodeBitmap = codeBitmap;
        this.mWebUrl = webUrl;
        this.mShareBg = shareBg;
        this.mGeneratePictureManager = GeneratePictureUtils.getInstance();
    }

    public void startPrepare(GeneratePictureUtils.OnGenerateListener listener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mShareView = LayoutInflater.from(mContext).inflate(R.layout.bean_share_pic, mRootView, false);
                ImageView mIvQrCode = mShareView.findViewById(R.id.iv_qrCode_sharePic);
                ImageView ivBg = mShareView.findViewById(R.id.iv_bg);
                TextView tvWebUrl = mShareView.findViewById(R.id.tv_web_url);
                if (mWebUrl != null) {
                    tvWebUrl.setText(mContext.getString(R.string.share_web_url_qr_code, mWebUrl));
                }
                mIvQrCode.setImageBitmap(mCodeBitmap);
                if (mShareBg != null) {
                    GlideApp.with(mContext)
                            .load(mShareBg)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    ivBg.setBackground(resource);
                                    prepared(listener);
                                }
                            });
                } else {
                    prepared(listener);
                }
            }
        });
    }

    public View getView() {
        if (null != mShareView) {
            return mShareView;
        }
        return null;
    }

    protected void prepared(GeneratePictureUtils.OnGenerateListener listener) {
        mGeneratePictureManager.prepared(this, listener);
    }

    public String getSavePath() {
        return mSavePath;
    }

    public void setSavePath(String savePath) {
        mSavePath = savePath;
    }

}
