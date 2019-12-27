package com.video.test.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.javabean.PlayerUrlListBean;

import java.util.ArrayList;
import java.util.List;

import static com.video.test.ui.adapter.PlayerChooseVideoAdapter.ViewHolder;

/**
 * 视频横屏播放时选择分集的adapter
 *
 * @author : AhhhhDong
 * @date : 2019/5/6 15:29
 */
public class PlayerChooseVideoAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<PlayerUrlListBean> mVideoList = new ArrayList<>();

    private OnVideoSelectedListener mSelectedListener = null;

    private int mCurrentPosition = -1;

    public void setSelectedListener(OnVideoSelectedListener selectedListener) {
        this.mSelectedListener = selectedListener;
    }

    public void setVideoList(List<PlayerUrlListBean> videoList) {
        mVideoList = videoList;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public List<PlayerUrlListBean> getVideoList() {
        return mVideoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bean_recycle_item_choose_video, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PlayerUrlListBean bean = mVideoList.get(position);
        viewHolder.tvVideoName.setText(bean.getVideoName().trim());
        if (mCurrentPosition == position) {
            viewHolder.tvVideoName.setTextColor(ContextCompat.getColor(viewHolder.tvVideoName.getContext(), R.color.videoPlayer_font_recycle_item_selected));
        } else {
            viewHolder.tvVideoName.setTextColor(ContextCompat.getColor(viewHolder.tvVideoName.getContext(), R.color.colorWhite));
        }
        viewHolder.tvVideoName.setOnClickListener(view -> {
            if (mCurrentPosition == position) {
                return;
            }
            mCurrentPosition = position;
            if (mSelectedListener != null) {
                mSelectedListener.onVideoSelected(position, bean);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(int position, PlayerUrlListBean bean);
    }
}
