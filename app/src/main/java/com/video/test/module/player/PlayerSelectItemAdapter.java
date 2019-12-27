package com.video.test.module.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class PlayerSelectItemAdapter extends RecyclerView.Adapter<PlayerSelectItemAdapter.ViewHolder> {
    private static final String TAG = "PlayerSelectItemAdapter";

    private List<PlayerUrlListBean> mPlayerUrlList;
    private List<PlayerUrlListBean> mCurrentPlayerUrlList;
    private PlayerContract.Presenter mPlayerPresenter;
    private PlayerUrlListBean mCurrentBean;


    PlayerSelectItemAdapter(PlayerContract.Presenter presenter) {
        this.mPlayerPresenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.d(TAG, "viewType == " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_video_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PlayerUrlListBean playerUrlListBean = mCurrentPlayerUrlList.get(position);
        holder.mCbVideoName.setText(playerUrlListBean.getVideoName());
        holder.mCbVideoName.setChecked(mCurrentBean == playerUrlListBean);
        holder.mCbVideoName.setOnClickListener(v -> {
            mCurrentBean = playerUrlListBean;
            notifyDataSetChanged();
            LogUtils.d(TAG, "mCbVideoName videoName== " + playerUrlListBean.getVideoName()
                    + "url == " + playerUrlListBean.getVideoUrl());
            //通知其他地方的视频列表刷新
            mPlayerPresenter.setItemSelectedPosition(mPlayerUrlList.indexOf(playerUrlListBean));
            mPlayerPresenter.getVideoPlayUrl(playerUrlListBean.getVideoUrl(), playerUrlListBean.getVideoName());
        });
    }


    @Override
    public int getItemCount() {
        return null == mCurrentPlayerUrlList ? 0 : mCurrentPlayerUrlList.size();
    }


    void setHistoryPosition(int position) {
        if (position >= 0 && position < mPlayerUrlList.size()) {
            mCurrentBean = mPlayerUrlList.get(position);
        }
    }

    public void setData(List<PlayerUrlListBean> playerUrlListBeans) {
        this.mPlayerUrlList = playerUrlListBeans;
    }

    public void setCurrentData(List<PlayerUrlListBean> playerUrlListBeans) {
        mCurrentPlayerUrlList = playerUrlListBeans;
        notifyDataSetChanged();
    }

    static class ViewHolder extends BaseViewHolder<List<PlayerUrlListBean>> {

        @BindView(R.id.cb_recycle_item_select)
        AppCompatCheckBox mCbVideoName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void bindData(int position, int viewType, List<PlayerUrlListBean> playerUrlListBeans) {

        }
    }
}
