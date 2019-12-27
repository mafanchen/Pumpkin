package com.video.test.module.topicvideolist;


import android.content.Context;
import android.support.annotation.NonNull;
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
public class TopicVideoListAdapter extends RecyclerView.Adapter<TopicVideoListAdapter.TopicVideoListViewHolder> {
    private static final String TAG = "TopicVideoListAdapter";

    private List<VideoBean> mVideoBeanList;

    @NonNull
    @Override
    public TopicVideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_video, parent, false);
        return new TopicVideoListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TopicVideoListViewHolder holder, int position) {
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
        notifyDataSetChanged();
    }


    static class TopicVideoListViewHolder extends BaseViewHolder<List<VideoBean>> {

        private final Context context;
        @BindView(R.id.iv_recycle_item_pic_video)
        ImageView mIvPic;
        @BindView(R.id.tv_recycle_item_point_video)
        TextView mTvPoint;
        @BindView(R.id.tv_recycle_item_videoName_video)
        TextView mTvVideoName;

        TopicVideoListViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }


        @Override
        public void bindData(int position, int viewType, List<VideoBean> data) {
            final VideoBean videoBean = data.get(position);
            mTvVideoName.setText(videoBean.getVod_name());
            String vodScore = videoBean.getVod_scroe();
            if (TextUtils.isEmpty(vodScore) || Double.parseDouble(vodScore) == 0 || Double.parseDouble(vodScore) == 10) {
                vodScore = "暂无评";
            }
            vodScore = itemView.getResources().getString(R.string.video_point, vodScore);
            mTvPoint.setText(vodScore);
            GlideApp.with(context).load(videoBean.getVod_pic()).transition(withCrossFade()).into(mIvPic);

            LogUtils.d(TAG, "bindData score : " + vodScore + " name : " + videoBean.getVod_name());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "mFl Click id== " + videoBean.getVod_id() + " Name == " + videoBean.getVod_name());
                    ARouter.getInstance().build("/player/activity").withString("vodId", videoBean.getVod_id()).navigation();
                }
            });
        }

    }
}
