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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoTitleBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author drakeet
 */
public class RecommendVideoTitleItemViewBinder extends ItemViewBinder<VideoTitleBean, RecommendVideoTitleItemViewBinder.ViewHolder> {

    private static final String TAG = "RecommendVideoTitleItemViewBinder";

    @Override
    protected @NonNull
    ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_video_column_recommend, parent, false));

    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull VideoTitleBean videoTitleBean) {
        if (null != holder.mTvTitle) {
            holder.mTvTitle.setText(videoTitleBean.getType());
            GlideApp.with(holder.itemView.getContext()).load(videoTitleBean.getTypePic()).into(holder.mIvTitlePic);
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title_video_column_recommend)
        TextView mTvTitle;
        @BindView(R.id.iv_title_video_column_recommend)
        ImageView mIvTitlePic;
        @BindView(R.id.divider)
        View dividerTop;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
