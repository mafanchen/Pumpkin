package com.video.test.module.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class PlayerDownloadSelectItemAdapter extends RecyclerView.Adapter<PlayerDownloadSelectItemAdapter.ViewHolder> {
    private static final String TAG = "PlayerDownloadSelectItemAdapter";

    private List<PlayerUrlListBean> mPlayerUrlList;
    private PlayerContract.Presenter mPlayerPresenter;
    private String mVideoId;
    private String mVideoName;


    PlayerDownloadSelectItemAdapter(PlayerContract.Presenter presenter) {
        this.mPlayerPresenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_player_download_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PlayerUrlListBean item = mPlayerUrlList.get(position);

        String videoUrl = item.getVideoUrl();
        String videoItemName = item.getVideoName();

        switch (item.getDownloadStatus()) {
            case AppConstant.M3U8_TASK_PENDING:
            case AppConstant.M3U8_TASK_PREPARE:
            case AppConstant.M3U8_TASK_DOWNLOADING:
            case AppConstant.M3U8_TASK_ERROR:
            case AppConstant.M3U8_TASK_PAUSE:
            case AppConstant.M3U8_TASK_ENOSPC:
                holder.mIVStatus.setVisibility(View.VISIBLE);
                holder.mIVStatus.setImageResource(R.drawable.ic_status_downloading);
                break;
            case AppConstant.M3U8_TASK_SUCCESS:
                holder.mIVStatus.setVisibility(View.VISIBLE);
                holder.mIVStatus.setImageResource(R.drawable.ic_status_ready);
                break;
            default:
                holder.mIVStatus.setVisibility(View.GONE);
                break;
        }

        holder.mTvName.setText(videoItemName);
        holder.itemView.setOnClickListener(v -> {

            LogUtils.d(TAG, "onClickListener videoId : " + mVideoId
                    + " videoName : " + mVideoName + item.getVideoName()
                    + " mVideoUrl : " + item.getVideoUrl());

            switch (item.getDownloadStatus()) {
//                case AppConstant.M3U8_TASK_PENDING:
//                case AppConstant.M3U8_TASK_PREPARE:
//                case AppConstant.M3U8_TASK_DOWNLOADING:
//                    item.setDownloadStatus(AppConstant.M3U8_TASK_PAUSE);
//                    mPlayerPresenter.setDownloadUrl(videoUrl, mVideoId, mVideoName + videoItemName);
//                    notifyDataSetChanged();
//                    break;
                case AppConstant.M3U8_TASK_PENDING:
                case AppConstant.M3U8_TASK_PREPARE:
                case AppConstant.M3U8_TASK_DOWNLOADING:
                case AppConstant.M3U8_TASK_ERROR:
                case AppConstant.M3U8_TASK_PAUSE:
                case AppConstant.M3U8_TASK_ENOSPC:
                    item.setDownloadStatus(AppConstant.M3U8_TASK_DEFAULT);
                    mPlayerPresenter.deleteDownloadTask(videoUrl);
                    notifyDataSetChanged();
                    break;
                case AppConstant.M3U8_TASK_SUCCESS:
                    ToastUtils.showToast(TestApp.getContext(), "已下载完成");
                    break;
                default:
                    item.setDownloadStatus(AppConstant.M3U8_TASK_DOWNLOADING);
                    mPlayerPresenter.setDownloadUrl(videoUrl, mVideoId, mVideoName, videoItemName);
                    notifyDataSetChanged();
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mPlayerUrlList ? 0 : mPlayerUrlList.size();
    }

    void setVideoInfo(String videoId, String videoName) {
        this.mVideoId = videoId;
        this.mVideoName = videoName;

    }

    public void setData(List<PlayerUrlListBean> playerUrlListBeanList) {
        if (null == mPlayerUrlList) {
            mPlayerUrlList = new ArrayList<>(playerUrlListBeanList);
        } else {
            mPlayerUrlList.clear();
            mPlayerUrlList.addAll(playerUrlListBeanList);
        }
        notifyDataSetChanged();
    }

    public List<PlayerUrlListBean> getData() {
        return mPlayerUrlList;
    }

    static class ViewHolder extends BaseViewHolder<List<PlayerUrlListBean>> {

        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.iv_download_status)
        ImageView mIVStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<PlayerUrlListBean> playerUrlListBeans) {

        }
    }
}
