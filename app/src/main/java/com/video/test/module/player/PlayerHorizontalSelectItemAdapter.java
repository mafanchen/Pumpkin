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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class PlayerHorizontalSelectItemAdapter extends RecyclerView.Adapter<PlayerHorizontalSelectItemAdapter.ViewHolder> {
    private static final String TAG = "PlayerHorizontalSelectItemAdapter";

    private List<PlayerUrlListBean> mPlayerUrlList;
    private PlayerContract.Presenter mPlayerPresenter;
    private int mSelectedPos = -1;

    PlayerHorizontalSelectItemAdapter(PlayerContract.Presenter presenter) {
        this.mPlayerPresenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.d(TAG, "viewType == " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_short_video_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull List<Object> payloads) {
        final PlayerUrlListBean playerUrlListBean = mPlayerUrlList.get(position);
        if (payloads.isEmpty()) {
            holder.mCbVideoName.setText(playerUrlListBean.getVideoName());
            holder.mCbVideoName.setChecked(mSelectedPos == position);
        } else {
            holder.mCbVideoName.setChecked(mSelectedPos == position);
        }

        holder.mCbVideoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPos != position) {
                    holder.mCbVideoName.setChecked(true);
                    if (mSelectedPos != -1) {
                        notifyItemChanged(mSelectedPos, 0);
                    }
                }
                mSelectedPos = position;
                LogUtils.d(TAG, "mCbVideoName videoName== " + playerUrlListBean.getVideoName()
                        + "url == " + playerUrlListBean.getVideoUrl());
                if (null != mPlayerPresenter) {
                    mPlayerPresenter.setItemSelectedPosition(mSelectedPos);
                    mPlayerPresenter.getVideoPlayUrl(playerUrlListBean.getVideoUrl(), playerUrlListBean.getVideoName());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == mPlayerUrlList ? 0 : mPlayerUrlList.size();
    }

    public void setHistoryPosition(int position) {
        this.mSelectedPos = position;
    }

    public void setData(List<PlayerUrlListBean> playerUrlListBeans) {
        if (null == mPlayerUrlList) {
            mPlayerUrlList = new ArrayList<>(playerUrlListBeans);
        } else {
            mPlayerUrlList.clear();
            mPlayerUrlList.addAll(playerUrlListBeans);
        }
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
