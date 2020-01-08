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

    private boolean isSpecial = false;

    public VideoRecommendViewBinder() {
    }

    public VideoRecommendViewBinder(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    @NonNull
    @Override
    protected VideoRecommendViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        int viewId = isSpecial ? R.layout.bean_recycle_item_video_special : R.layout.bean_recycle_item_video;
        View view = inflater.inflate(viewId, parent, false);
        return new VideoRecommendViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoRecommendViewHolder holder, @NonNull final VideoBean videoBean) {
        holder.mTvVideoName.setText(videoBean.getVod_name());
        GlideApp.with(holder.itemView.getContext())
                .load(videoBean.getVod_pic())
                .override(holder.mIvPic.getWidth(), holder.mIvPic.getHeight())
                .centerCrop()
                .transition(withCrossFade())
                .error(R.drawable.bg_video_default_vertical)
                .into(holder.mIvPic);
        holder.itemView.setOnClickListener(v -> {
            LogUtils.i(TAG, "mFl Click == " + videoBean.toString());
            ARouter.getInstance().build("/player/activity").withString("vodId", videoBean.getVod_id()).withString("vodPid", String.valueOf(videoBean.getVodPid())).navigation();
        });
        setScore(holder.mTvPoint, videoBean.getVodType(), videoBean.getVod_continu(), videoBean.getVod_scroe(), videoBean.isVodIsEnd());
    }

    private void setScore(TextView tvScore, String vodType, String vodContinue, String score, boolean isEnd) {
        //continue 字段等于0说明视频有多集
        if (TextUtils.isEmpty(vodContinue) || Integer.parseInt(vodContinue) == 0) {
            //不连载，显示豆瓣评分
            if (TextUtils.isEmpty(score) || Double.parseDouble(score) == 0 || Double.parseDouble(score) == 10) {
                tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                tvScore.setText("暂无评分");
            } else {
                tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_grade));
                tvScore.setText(score);
            }
        }
        //视频已经完结 ,综艺节目始终显示更新至xx期
        else if (isEnd && !TextUtils.equals(vodType, "3")) {
            tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
            tvScore.setText(tvScore.getResources().getString(R.string.video_episode_all, vodContinue));
        }
        //视频还在更新
        else {
            //长度小于4 说明是电视剧或连载动漫
            if (vodContinue.length() <= 4) {
                tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                tvScore.setText(tvScore.getResources().getString(R.string.video_episode, vodContinue));
            }
            //大于4 说明是综艺节目
            else {
                tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                tvScore.setText(tvScore.getResources().getString(R.string.video_stage, vodContinue));
            }
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
