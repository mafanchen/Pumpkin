package com.video.test.module.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.PlayerRecommendListBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class PlayerRecommendItemAdapter extends RecyclerView.Adapter<PlayerRecommendItemAdapter.ViewHolder> {
    private static final String TAG = "PlayerRecommendItemAdapter";
    private List<PlayerRecommendListBean> mPlayerRecommendListBeans;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PlayerRecommendListBean playerRecommendListBean = mPlayerRecommendListBeans.get(position);
        holder.bindData(position, holder.getItemViewType(), playerRecommendListBean);
        holder.mIvPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "mLl == " + playerRecommendListBean.toString());
                ARouter.getInstance().build("/player/activity").withString("vodId", playerRecommendListBean.getVod_id()).navigation();
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == mPlayerRecommendListBeans ? 0 : mPlayerRecommendListBeans.size();
    }


    public void setData(List<PlayerRecommendListBean> playerUrlListBeanList) {
        if (null == mPlayerRecommendListBeans) {
            mPlayerRecommendListBeans = new ArrayList<>(playerUrlListBeanList);
        } else {
            mPlayerRecommendListBeans.clear();
            mPlayerRecommendListBeans.addAll(playerUrlListBeanList);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends BaseViewHolder<PlayerRecommendListBean> {

        private final Context context;
        @BindView(R.id.iv_recycle_item_pic_video)
        ImageView mIvPic;
        @BindView(R.id.tv_recycle_item_point_video)
        TextView mtvPoint;
        @BindView(R.id.tv_recycle_item_videoName_video)
        TextView mTvVideoName;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, final PlayerRecommendListBean playerRecommendListBean) {
            GlideApp.with(context).load(playerRecommendListBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvPic);
            String score = itemView.getResources().getString(R.string.video_point, playerRecommendListBean.getVod_scroe());
            mtvPoint.setText(score);
            mTvVideoName.setText(playerRecommendListBean.getVod_name());
        }
    }
}
