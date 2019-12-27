package com.video.test.module.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.utils.LogUtils;
import com.video.test.utils.UnicodeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class PlayerCommentItemAdapter extends RecyclerView.Adapter<PlayerCommentItemAdapter.ViewHolder> {
    private static final String TAG = "PlayerCommentItemAdapter";
    private List<VideoCommentBean> mVideoCommentList;
    private int mCurrentCommentNum;
    private int mTotalCommentNum;
    private String vodId;

    public PlayerCommentItemAdapter() {
        this.mCurrentCommentNum = 8;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.d(TAG, "viewType == " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_video_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bindData(position, holder.getItemViewType(), mVideoCommentList);
    }

    @Override
    public int getItemCount() {
        if (null != mVideoCommentList) {
            if (mCurrentCommentNum < mTotalCommentNum) {
                return mCurrentCommentNum;
            } else {
                return mTotalCommentNum;
            }
        } else {
            return 0;
        }
    }

    public void moreVideoComment() {
        LogUtils.d(TAG, "moreVideoComment size : " + mCurrentCommentNum);
        mCurrentCommentNum += 8;
        notifyDataSetChanged();
    }

    public void setData(List<VideoCommentBean> videoCommentBeans, String vodId) {
        if (!TextUtils.equals(this.vodId, vodId)) {
            mCurrentCommentNum = 8;
        }
        this.vodId = vodId;
        this.mTotalCommentNum = videoCommentBeans.size();
        if (null == mVideoCommentList) {
            mVideoCommentList = new ArrayList<>(videoCommentBeans);
        } else {
            mVideoCommentList.clear();
            mVideoCommentList.addAll(videoCommentBeans);
        }
        notifyDataSetChanged();
    }

    /**
     * 判断是否还有更多的评论
     *
     * @return
     */
    public boolean hasMoreData() {
        return mCurrentCommentNum < mTotalCommentNum;
    }


    static class ViewHolder extends BaseViewHolder<List<VideoCommentBean>> {

        private final Context context;
        @BindView(R.id.civ_avatar_recycler_videoComment)
        CircleImageView civAvatar;
        @BindView(R.id.tv_nickname_recycler_videoComment)
        TextView tvNickname;
        @BindView(R.id.iv_crown_recycler_videoComment)
        ImageView ivCrown;
        @BindView(R.id.tv_commentTime_recycler_videoComment)
        TextView tvCommentTime;
        @BindView(R.id.tv_comment_recycler_videoComment)
        TextView tvComment;


        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<VideoCommentBean> commentBeanList) {
            if (null != commentBeanList) {
                VideoCommentBean videoCommentBean = commentBeanList.get(position);
                String avatarUrl = videoCommentBean.getPic();
                String userLevel = String.valueOf(videoCommentBean.getIs_vip());
                tvNickname.setText(videoCommentBean.getUsername());
                tvCommentTime.setText(videoCommentBean.getAdd_time());
                String comment = UnicodeUtils.unicodeToString(videoCommentBean.getVod_comment());
                LogUtils.d(TAG, "comment : " + comment);
                tvComment.setText(comment);

                if (!avatarUrl.isEmpty()) {
                    LogUtils.d(TAG, "bindData avatar url : " + avatarUrl);
                    GlideApp.with(context).load(avatarUrl).into(civAvatar);
                } else {
                    GlideApp.with(context).load(R.drawable.ic_avatar_default).into(civAvatar);
                }

                if (AppConstant.USER_VIP.equals(userLevel) || AppConstant.USER_VIP_LASTDAY.equals(userLevel)) {
                    ivCrown.setVisibility(View.VISIBLE);
                    tvNickname.setTextColor(context.getResources().getColor(R.color.player_videoComment_nickName_vip));
                } else {
                    ivCrown.setVisibility(View.GONE);
                    tvNickname.setTextColor(context.getResources().getColor(R.color.player_videoComment_nickName));
                }

            } else {
                LogUtils.d(TAG, "PlayerCommentItemAdapter = null");
            }
        }
    }
}
