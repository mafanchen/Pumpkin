package com.video.test.module.screenvideo;

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
import com.video.test.framework.BaseViewHolder;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class ScreenVideoListAdapter extends RecyclerView.Adapter<ScreenVideoListAdapter.VideoListViewHolder> {
    private static final String TAG = "VideoListAdapter";


    private List<VideoBean> mVideoBeanList;


    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_video, parent, false);
        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mVideoBeanList);
    }

    @Override
    public int getItemCount() {
        return null == mVideoBeanList ? 0 : mVideoBeanList.size();
    }

    public void setData(List<VideoBean> videoBeanList) {
        if (null == mVideoBeanList) {
            mVideoBeanList = new ArrayList<>();
            mVideoBeanList.addAll(videoBeanList);
        } else {
            mVideoBeanList.clear();
            mVideoBeanList.addAll(videoBeanList);
        }
        notifyDataSetChanged();
    }

    public void addData(List<VideoBean> videoBeans) {
        if (null != mVideoBeanList) {
            mVideoBeanList.addAll(videoBeans);
        }
    }


    static class VideoListViewHolder extends BaseViewHolder<List<VideoBean>> {

        @BindView(R.id.iv_recycle_item_pic_video)
        ImageView mIvPic;
        @BindView(R.id.tv_recycle_item_point_video)
        TextView mTvPoint;
        @BindView(R.id.tv_recycle_item_videoName_video)
        TextView mTvVideoName;

        VideoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<VideoBean> data) {
            final VideoBean videoBean = data.get(position);
            mTvVideoName.setText(videoBean.getVod_name());
            String vodContinu = videoBean.getVod_continu();
            if (TextUtils.isEmpty(vodContinu) || Integer.parseInt(vodContinu) == 0) {
                //不连载，显示豆瓣评分
                String vodScore = videoBean.getVod_scroe();
                if (TextUtils.isEmpty(vodScore) || Double.parseDouble(vodScore) == 0 || Double.parseDouble(vodScore) == 10) {
                    mTvPoint.setTextColor(ContextCompat.getColor(mTvPoint.getContext(), R.color.homepage_font_episode));
                    mTvPoint.setText("暂无评分");
                } else {
                    mTvPoint.setTextColor(ContextCompat.getColor(mTvPoint.getContext(), R.color.homepage_font_grade));
                    mTvPoint.setText(vodScore);
                }
            } else if (vodContinu.length() <= 4) {
                mTvPoint.setTextColor(ContextCompat.getColor(mTvPoint.getContext(), R.color.homepage_font_episode));
                mTvPoint.setText(itemView.getResources().getString(R.string.video_episode, vodContinu));
            } else {
                mTvPoint.setTextColor(ContextCompat.getColor(mTvPoint.getContext(), R.color.homepage_font_episode));
                mTvPoint.setText(itemView.getResources().getString(R.string.video_stage, vodContinu));
            }
            GlideApp.with(itemView.getContext()).load(videoBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvPic);
            itemView.setOnClickListener(v -> {
                LogUtils.d(TAG, "mFl Click id== " + videoBean.getVod_id() + " Name == " + videoBean.getVod_name());
                ARouter.getInstance().build("/player/activity").withString("vodId", videoBean.getVod_id()).navigation();
            });
        }


    }
}
