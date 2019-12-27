package com.video.test.module.notice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.NoticeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private static final String TAG = "NoticeAdapter";


    private List<NoticeBean> mVideoBeanList;


    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mVideoBeanList);
    }

    @Override
    public int getItemCount() {
        return null == mVideoBeanList ? 0 : mVideoBeanList.size();
    }

    public void setData(List<NoticeBean> noticeBeanList) {
        if (null == mVideoBeanList) {
            mVideoBeanList = new ArrayList<>();
            mVideoBeanList.addAll(noticeBeanList);
        } else {
            mVideoBeanList.clear();
            mVideoBeanList.addAll(noticeBeanList);
        }
        notifyDataSetChanged();
    }


    static class NoticeViewHolder extends BaseViewHolder<List<NoticeBean>> {

        @BindView(R.id.tv_title_notice)
        TextView mTvTitle;
        @BindView(R.id.tv_publishTime_notice)
        TextView mTvPublishTime;
        @BindView(R.id.tv_content_notice)
        TextView mTvContent;
        @BindView(R.id.tv_webTitle_notice)
        TextView mTvWebTitle;
        @BindView(R.id.tv_webUrl_notice)
        TextView mTvWebUrl;

        private NoticeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<NoticeBean> data) {
            NoticeBean noticeBean = data.get(position);
            mTvTitle.setText(noticeBean.getTitle());
            mTvPublishTime.setText(noticeBean.getTime());
            mTvContent.setText(noticeBean.getContent());

            String webTitle = noticeBean.getWebTitle();
            if (webTitle.isEmpty()) {
                mTvWebTitle.setVisibility(View.GONE);
            } else {
                mTvWebTitle.setVisibility(View.VISIBLE);
                mTvWebTitle.setText(webTitle);
            }

            String webUrl = noticeBean.getWebUrl();
            if (webTitle.isEmpty()) {
                mTvWebUrl.setVisibility(View.GONE);
            } else {
                mTvWebUrl.setVisibility(View.VISIBLE);
                mTvWebUrl.setText(webUrl);
            }
        }
    }
}
