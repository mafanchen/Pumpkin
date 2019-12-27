package com.video.test.module.videorecommend;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/2.
 */
public class VideoRecommendViewBinder extends ItemViewBinder<VideoBean, VideoRecommendViewBinder.VideoRecommendViewHolder> {
    private static final String TAG = "VideoRecommendViewBinder";


    @NonNull
    @Override
    protected VideoRecommendViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycle_item_video, parent, false);
        return new VideoRecommendViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoRecommendViewHolder holder, @NonNull final VideoBean videoBean) {
        holder.mTvVideoName.setText(videoBean.getVod_name());
        GlideApp.with(holder.itemView.getContext()).load(videoBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(holder.mIvPic);
        holder.itemView.setOnClickListener(v -> {
            LogUtils.i(TAG, "mFl Click == " + videoBean.toString());
            ARouter.getInstance().build("/player/activity").withString("vodId", videoBean.getVod_id()).navigation();
        });
        String vodContinu = videoBean.getVod_continu();
        if (TextUtils.isEmpty(vodContinu) || Integer.parseInt(vodContinu) == 0) {
            //不连载，显示豆瓣评分
            String vodScore = videoBean.getVod_scroe();
            if (TextUtils.isEmpty(vodScore) || Double.parseDouble(vodScore) == 0 || Double.parseDouble(vodScore) == 10) {
                holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
                holder.mTvPoint.setText("暂无评分");
            } else {
                holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_grade));
                holder.mTvPoint.setText(vodScore);
            }
        } else if (vodContinu.length() <= 4) {
            holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
            holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_episode, vodContinu));
        } else {
            holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
            holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_stage, vodContinu));
        }
    }


    static class VideoRecommendViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_recycle_item_pic_video)
        ImageView mIvPic;
        @BindView(R.id.tv_recycle_item_point_video)
        TextView mTvPoint;
        @BindView(R.id.tv_recycle_item_videoName_video)
        TextView mTvVideoName;

        VideoRecommendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
