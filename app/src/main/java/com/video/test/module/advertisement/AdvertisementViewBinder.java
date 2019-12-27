package com.video.test.module.advertisement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/2.
 */
public class AdvertisementViewBinder extends ItemViewBinder<VideoBean, AdvertisementViewBinder.CartoonViewHolder> {
    private static final String TAG = "AdvertisementViewBinder";

    @NonNull
    @Override
    protected CartoonViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycle_item_video, parent, false);
        return new CartoonViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartoonViewHolder holder, @NonNull VideoBean videoBean) {
        String score = holder.itemView.getResources().getString(R.string.video_point, videoBean.getVod_scroe());
        holder.mTvPoint.setText(score);
        holder.mTvVideoName.setText(videoBean.getVod_name());
        GlideApp.with(holder.context).load(videoBean.getVod_pic()).transition(withCrossFade()).into(holder.mIvPic);
    }


    static class CartoonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        @BindView(R.id.iv_recycle_item_pic_video)
        ImageView mIvPic;
        @BindView(R.id.tv_recycle_item_point_video)
        TextView mTvPoint;
        @BindView(R.id.tv_recycle_item_videoName_video)
        TextView mTvVideoName;

        public CartoonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: mFl");

        }
    }
}
