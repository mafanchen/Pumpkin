package com.video.test.module.profilepic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.ProfilePictureBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProfilePicViewBinder extends ItemViewBinder<ProfilePictureBean, ProfilePicViewBinder.ProfilePicViewHolder> {
    private static final String TAG = "ProfilePicViewBinder";
    private String mSelectedUrl;
    private int mPosition = -1;
    private ProfilePicSelectedListener mListener;


    public ProfilePicViewBinder(String selectedUrl, ProfilePicSelectedListener listener) {
        this.mSelectedUrl = selectedUrl;
        this.mListener = listener;
    }

    @NonNull
    @Override
    protected ProfilePicViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycle_item_profile_picture, parent, false);
        return new ProfilePicViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProfilePicViewHolder holder, @NonNull ProfilePictureBean item) {
        GlideApp.with(holder.itemView.getContext())
                .load(item.getPicUrl())
                .transition(withCrossFade())
                .into(holder.mIvPic);

        if (TextUtils.equals(mSelectedUrl, item.getPicUrl())) {
            LogUtils.d(TAG, "have default pics url : " + mSelectedUrl);
            holder.mFlBackground.setVisibility(View.VISIBLE);
            holder.mCheck.setChecked(true);
            mPosition = getPosition(holder);
            // 第一次需要 url 来确定 postion 位置 对比之后 就不使用URL
            mSelectedUrl = "";
        }

        LogUtils.d(TAG, "selected position : " + mPosition);

        if (mPosition ==

                getPosition(holder)) {
            holder.mFlBackground.setVisibility(View.VISIBLE);
            holder.mCheck.setChecked(true);
        } else if (mPosition !=

                getPosition(holder)) {
            holder.mFlBackground.setVisibility(View.INVISIBLE);
        }


        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition != getPosition(holder)) {
                    mPosition = getPosition(holder);
                } else {
                    mPosition = getPosition(holder);
                    mPosition = -1;
                }
                if (null != mListener) {
                    mListener.picSelected(item.getPicId(), item.getPicUrl());
                }
                LogUtils.d(TAG, "mFrameLayout click id  = " + item.getPicId());
            }
        });
    }


    static class ProfilePicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_picture_profilePic)
        ImageView mIvPic;
        @BindView(R.id.cb_recycle_item_history)
        CheckBox mCheck;
        @BindView(R.id.fl_profilePic)
        FrameLayout mFrameLayout;
        @BindView(R.id.fl_cb_background_profilePic)
        FrameLayout mFlBackground;

        public ProfilePicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
