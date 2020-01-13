/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.video.test.ui.viewbinder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoTitleBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author drakeet
 */
public class VideoTitleItemViewBinder extends ItemViewBinder<VideoTitleBean, VideoTitleItemViewBinder.ViewHolder> {

    private static final String TAG = "VideoTitleItemViewBinder";

    private boolean isSpecial = false;

    public VideoTitleItemViewBinder() {
    }

    public VideoTitleItemViewBinder(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    @Override
    protected @NonNull
    ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_video_column, parent, false));
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final VideoTitleBean videoTitleBean) {
        if (null != holder.mTvTitle) {
            holder.mDivider.setVisibility(videoTitleBean.isShowDivider() ? View.VISIBLE : View.GONE);
            if (isSpecial) {
                holder.mDivider.setBackgroundColor(Color.TRANSPARENT);
                holder.mTvMore.setTextColor(Color.parseColor("#333333"));
                holder.mTvMore.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_more_black, 0);
            }
            holder.mTvTitle.setText(videoTitleBean.getType());
            GlideApp.with(holder.itemView.getContext()).load(videoTitleBean.getTypePic()).into(holder.mIvTitlePic);
            switch (videoTitleBean.getColumnType()) {
                case VideoTitleBean.TYPE_RECOMMEND:
                    holder.mTvMore.setVisibility(View.VISIBLE);
                    holder.mTvMore.setOnClickListener(v -> jump2RecommendList(videoTitleBean.getParentId(), videoTitleBean.getType(), videoTitleBean.getPid()));
                    break;
                case VideoTitleBean.TYPE_HOTEST:
                    holder.mTvMore.setVisibility(View.VISIBLE);
                    holder.mTvMore.setOnClickListener(v -> jump2Hottest(videoTitleBean.getShowId(), videoTitleBean.getType(), videoTitleBean.getPid()));
                    break;
                case VideoTitleBean.TYPE_CATEGORY:
                    holder.mTvMore.setVisibility(View.VISIBLE);
                    holder.mTvMore.setOnClickListener(v -> {
                        LogUtils.i(TAG, " More Pid = " + videoTitleBean.getPid()
                                + " Tag = " + videoTitleBean.getTag()
                                + " Type = " + videoTitleBean.getType());
                        jump2VideoList(videoTitleBean);

                    });
                    break;
                default:
                    holder.mTvMore.setVisibility(View.GONE);
            }
        }
    }

    private void jump2RecommendList(String parentId, String title, int vodPid) {
        ARouter.getInstance().build("/recommend/activity")
                .withString("parentId", parentId)
                .withString("title", title)
                .withInt("vodPid", vodPid)
                .navigation();
    }


    private void jump2VideoList(VideoTitleBean videoTitleBean) {
        ARouter.getInstance().build("/screenVideo/activity")
                .withInt("pid", videoTitleBean.getPid())
                .withString("tag", videoTitleBean.getTag())
                .withString("tagName", videoTitleBean.getType())
                .navigation();
    }

    private void jump2Hottest(String showId, String title, int vodPid) {
        Postcard build = ARouter.getInstance().build("/hottest/activity");
        if (isSpecial) {
            //2019 showPid = 7,其他类型的页面不用传
            build.withString("showPid", "7");
        }
        build.withString("showId", showId)
                .withString("title", title)
                .withInt("vodPid", vodPid)
                .navigation();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title_video_column)
        TextView mTvTitle;
        @BindView(R.id.tv_more_video_column)
        TextView mTvMore;
        @BindView(R.id.iv_title_video_column)
        ImageView mIvTitlePic;
        @BindView(R.id.divider)
        View mDivider;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
