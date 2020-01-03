package com.video.test.module.videorecommend;

import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.framework.GlideRequest;
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

    public VideoRecommendHorizontalViewBinder() {
    }

    public VideoRecommendHorizontalViewBinder(boolean isSpecial) {
        this.isSpecial = isSpecial;
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
        GlideRequest<Drawable> request = GlideApp.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .override(324, 182)
                .centerCrop()
                .transition(withCrossFade());
        if (isSpecial) {
            request = request.transform(new RoundedCorners(15));
        }
        request.error(R.drawable.bg_video_default_horizontal)
                .into(holder.ivCover);
        holder.tvMainTitle.setText(item.getMainTitle());
        holder.tvSubTitle.setText(item.getSubTitle());
        holder.itemView.setOnClickListener(view -> {
            LogUtils.i(TAG, "video Click == " + item.toString());
            ARouter.getInstance().build("/player/activity").withString("vodId", item.getVideoId()).withString("vodPid", String.valueOf(item.getVodPid())).navigation();
        });
        String vodContinue = item.getVodContinue();
        //这里只有电视剧，才会有完结状态
        if (TextUtils.equals(item.getVodType(), "3")) {
            holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
            if (item.isVodIsEnd()) {
                holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_episode_all, vodContinue));
            } else {
                holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_episode, vodContinue));
            }
        } else {
            if (TextUtils.isEmpty(vodContinue) || Integer.parseInt(vodContinue) == 0) {
                //不连载，显示豆瓣评分
                String vodScore = item.getVodScore();
                if (TextUtils.isEmpty(vodScore) || Double.parseDouble(vodScore) == 0 || Double.parseDouble(vodScore) == 10) {
                    holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
                    holder.mTvPoint.setText("暂无评分");
                } else {
                    holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_grade));
                    holder.mTvPoint.setText(vodScore);
                }
            } else if (vodContinue.length() <= 4) {
                holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
                holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_episode, vodContinue));
            } else {
                holder.mTvPoint.setTextColor(ContextCompat.getColor(holder.mTvPoint.getContext(), R.color.homepage_font_episode));
                holder.mTvPoint.setText(holder.itemView.getResources().getString(R.string.video_stage, vodContinue));
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
