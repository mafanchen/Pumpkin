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
import com.video.test.javabean.VideoRecommendBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 视频列表顶部横向的视频
 *
 * @author : AhhhhDong
 * @date : 2019/3/19 15:13
 */
public class VideoRecommendHorizontalViewBinder extends ItemViewBinder<VideoRecommendBean, VideoRecommendHorizontalViewBinder.ViewHolder> {

    private static final String TAG = "VideoRecommendHorizontalViewBinder";

    private boolean isSpecial = false;
    private int vodPid;

    public VideoRecommendHorizontalViewBinder() {
    }

    public VideoRecommendHorizontalViewBinder(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public VideoRecommendHorizontalViewBinder(int vodPid) {
        this.vodPid = vodPid;
    }


    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        int layoutId = isSpecial ? R.layout.bean_recycle_item_recommend_video_special : R.layout.bean_recycle_item_recommend_video;
        View view = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull VideoRecommendBean item) {
        GlideApp.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .override(holder.ivCover.getWidth(), holder.ivCover.getHeight())
                .centerCrop()
                .transition(withCrossFade())
                .error(R.drawable.bg_video_default_horizontal)
                .into(holder.ivCover);
        holder.tvMainTitle.setText(item.getMainTitle());
        holder.tvSubTitle.setText(item.getSubTitle());
        holder.itemView.setOnClickListener(view -> {
            LogUtils.i(TAG, "video Click vodPid == " + vodPid);
            LogUtils.i(TAG, "video Click == " + item.toString());
            ARouter.getInstance().build("/player/activity").withString("vodId", item.getVideoId()).withString("vodPid", String.valueOf(item.getVodPid())).navigation();
        });
        setScore(holder.mTvPoint, item.getVodType(), item.getVodContinue(), item.getVodScore(), item.isVodIsEnd());
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
        //视频已经完结
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


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_main_title)
        TextView tvMainTitle;
        @BindView(R.id.tv_sub_title)
        TextView tvSubTitle;
        @BindView(R.id.tv_score)
        TextView mTvPoint;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
