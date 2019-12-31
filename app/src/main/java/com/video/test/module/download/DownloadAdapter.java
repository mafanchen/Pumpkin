package com.video.test.module.download;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jaygoo.library.m3u8downloader.bean.M3U8Task;
import jaygoo.library.m3u8downloader.utils.MUtils;

/**
 * @author Enoch Created on 2019/1/18.
 */
public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DownloadAdapter";

    private List<M3U8DownloadBean> data;
    private DownloadItemClickListener mDownloadListener;
    private boolean mIsManager = false;

    private static final int TYPE_DOWNLOADING = 1;
    private static final int TYPE_DOWNLOADED = 2;

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isDownloaded() ? TYPE_DOWNLOADED : TYPE_DOWNLOADING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == TYPE_DOWNLOADING) {
            View view = inflater.inflate(R.layout.bean_recycle_item_downloading, viewGroup, false);
            return new DownloadingViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.bean_recycle_item_downloaded, viewGroup, false);
            return new DownloadedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        M3U8DownloadBean bean = data.get(position);
        if (holder instanceof DownloadingViewHolder) {
            ((DownloadingViewHolder) holder).bindData(position, bean, mDownloadListener, mIsManager);
        } else if (holder instanceof DownloadedViewHolder) {
            ((DownloadedViewHolder) holder).bindData(position, bean, mDownloadListener, mIsManager);
        }
    }

    public void updateDownloadingStatus(M3U8Task m3u8) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (TextUtils.equals(data.get(i).getVideoUrl(), m3u8.getUrl()))
                    notifyItemChanged(i, m3u8);
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Object o = payloads.get(0);
            if (o instanceof M3U8Task && holder instanceof DownloadingViewHolder) {
                M3U8DownloadBean item = data.get(position);
                item.setProgress(((M3U8Task) o).getProgress());
                item.setTaskStatus(((M3U8Task) o).getState());
                ((DownloadingViewHolder) holder).setProgress(((M3U8Task) o).getProgress());
                ((DownloadingViewHolder) holder).setTaskStatus(((M3U8Task) o).getState());
            } else {
                onBindViewHolder(holder, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public void setData(List<M3U8DownloadBean> m3u8List) {
        if (null == data) {
            data = new ArrayList<>(m3u8List);
        } else {
            data.clear();
            data.addAll(m3u8List);
        }
        notifyDataSetChanged();
    }

    public List<M3U8DownloadBean> getData() {
        return data;
    }

    void setClickListener(DownloadItemClickListener downloadItemClickListener) {
        this.mDownloadListener = downloadItemClickListener;
    }

    void setIsManager(boolean isManager) {
        mIsManager = isManager;
    }

    void deSelectAll() {
        if (data != null) {
            for (Object o : data) {
                if (o instanceof M3U8DownloadBean) {
                    ((M3U8DownloadBean) o).setSelect(false);
                }
            }
        }
    }

    public void selectAll() {
        if (data != null) {
            for (Object o : data) {
                if (o instanceof M3U8DownloadBean) {
                    ((M3U8DownloadBean) o).setSelect(true);
                }
            }
        }
    }

    static class DownloadedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_recycle_videoName_download)
        TextView tvName;
        @BindView(R.id.tv_recycle_videoSize_download)
        TextView tvSize;
        @BindView(R.id.tv_recycle_history_download)
        TextView tvHistory;
        @BindView(R.id.checkbox_select)
        CheckBox checkBox;

        DownloadedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(int position, M3U8DownloadBean m3u8, DownloadItemClickListener listener, boolean isManager) {
            if (isManager) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(m3u8.isSelect());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        m3u8.setSelect(isChecked);
                        listener.onItemSelected(isChecked, m3u8);
                    }
            );
            if (m3u8.getLocalHistory() <= 0) {
                tvHistory.setText(R.string.activity_download_history_none);
            } else if (m3u8.getLocalHistory() >= 1) {
                tvHistory.setText(R.string.activity_download_history_watch_complete);
            } else {
                tvHistory.setText(tvHistory.getContext().getString(R.string.activity_download_history, m3u8.getLocalHistory() * 100));
            }
            tvSize.setText(MUtils.formatFileSize(m3u8.getTotalFileSize()));
            tvName.setText(m3u8.getVideoName());
            itemView.setOnClickListener(v -> {
                if (null != listener && !isManager) {
                    if (m3u8.isDownloaded()) {
                        LogUtils.d(TAG, "videoName : " + m3u8.getVideoName() + " videoUrl : " + m3u8.getVideoUrl());
                        listener.playLocalVideo(m3u8.getVideoUrl(), m3u8.getM3u8FilePath(), m3u8.getVideoName());
                    } else {
                        LogUtils.d(TAG, "videoName : " + m3u8.getVideoName() + " videoUrl : " + m3u8.getVideoUrl());
                        listener.playNetworkVideo(m3u8.getVideoId(), m3u8.getVideoUrl());
                    }
                }
            });
        }
    }

    static class DownloadingViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIvPic;
        private final TextView mTvVideoName;
        private final ProgressBar mProgressbar;
        private final TextView mTvTaskStatus;
        private final CheckBox mCheckBoxSelect;

        DownloadingViewHolder(View itemView) {
            super(itemView);
            mIvPic = itemView.findViewById(R.id.iv_recycle_pic_download);
            mTvVideoName = itemView.findViewById(R.id.tv_recycle_videoName_download);
            mProgressbar = itemView.findViewById(R.id.pb_recycle_download);
            mTvTaskStatus = itemView.findViewById(R.id.tv_recycle_taskStatus_download);
            mCheckBoxSelect = itemView.findViewById(R.id.checkbox_select);
        }

        private void showImage(@DrawableRes int imageId) {
            if (mIvPic == null) {
                return;
            }
            GlideApp.with(itemView.getContext())
                    .load(imageId)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                    .into(mIvPic);
        }

        public void bindData(int position, M3U8DownloadBean m3u8, DownloadItemClickListener listener, boolean isManager) {
            if (isManager) {
                mCheckBoxSelect.setVisibility(View.VISIBLE);
            } else {
                mCheckBoxSelect.setVisibility(View.GONE);
            }
//            this.m3U8Bean = m3u8;
            mCheckBoxSelect.setOnCheckedChangeListener(null);
            mCheckBoxSelect.setChecked(m3u8.isSelect());
            mCheckBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        m3u8.setSelect(isChecked);
                        listener.onItemSelected(isChecked, m3u8);
                    }
            );
            mTvVideoName.setText(m3u8.getVideoName());
            setProgress(m3u8.getProgress());
            setTaskStatus(m3u8.getTaskStatus());
            mIvPic.setOnClickListener(v -> {
                if (isManager) {
                    return;
                }
                LogUtils.d(TAG, "mCtvStartPause click");
                if (null != listener) {
                    switch (m3u8.getTaskStatus()) {
                        case AppConstant.M3U8_TASK_PENDING:
                        case AppConstant.M3U8_TASK_PREPARE:
                        case AppConstant.M3U8_TASK_DOWNLOADING:
                            listener.pauseTask(m3u8, position);
                            mTvTaskStatus.setText(R.string.activity_download_pause);
                            showImage(R.drawable.ic_download_paused);
                            break;
                        case AppConstant.M3U8_TASK_SUCCESS:
                            listener.playLocalVideo(m3u8.getVideoUrl(), m3u8.getM3u8FilePath(), m3u8.getVideoName());
                            break;
                        case AppConstant.M3U8_TASK_PAUSE:
                        case AppConstant.M3U8_TASK_ERROR:
                        case AppConstant.M3U8_TASK_ENOSPC:
                            listener.startTask(m3u8, position);
                            showImage(R.drawable.ic_download_downloading);
                            setProgress(m3u8.getProgress());
                            break;
                        default:
                            break;
                    }
                }
            });
            itemView.setOnClickListener(v -> {
                if (null != listener && !isManager) {
                    if (m3u8.isDownloaded()) {
                        LogUtils.d(TAG, "videoName : " + m3u8.getVideoName() + " videoUrl : " + m3u8.getVideoUrl());
                        listener.playLocalVideo(m3u8.getVideoUrl(), m3u8.getM3u8FilePath(), m3u8.getVideoName());
                    } else {
                        LogUtils.d(TAG, "videoName : " + m3u8.getVideoName() + " videoUrl : " + m3u8.getVideoUrl());
                        listener.playNetworkVideo(m3u8.getVideoId(), m3u8.getVideoUrl());
                    }
                }
            });
        }

        void setTaskStatus(int status) {
            switch (status) {
                case AppConstant.M3U8_TASK_PENDING:
                    showImage(R.drawable.ic_download_downloading);
                    mTvTaskStatus.setText("");
                    break;
                case AppConstant.M3U8_TASK_PREPARE:
                    mTvTaskStatus.setText(R.string.activity_download_prepare);
                    showImage(R.drawable.ic_download_downloading);
                    break;
                case AppConstant.M3U8_TASK_DOWNLOADING:
                    showImage(R.drawable.ic_download_downloading);
                    break;
                case AppConstant.M3U8_TASK_PAUSE:
                    showImage(R.drawable.ic_download_paused);
                    mTvTaskStatus.setText(R.string.activity_download_pause);
                    break;
                case AppConstant.M3U8_TASK_ERROR:
                case AppConstant.M3U8_TASK_ENOSPC:
                    showImage(R.drawable.ic_download_paused);
                    mTvTaskStatus.setText(R.string.activity_download_error);
                    break;
                case AppConstant.M3U8_TASK_SUCCESS:
                    showImage(R.drawable.ic_download_complete);
                    mTvTaskStatus.setText(R.string.activity_download_complete);
                    break;
                default:
                    break;
            }
        }

        void setProgress(double progress) {
            mProgressbar.setProgress((int) (progress * 100));
            mTvTaskStatus.setText(itemView.getContext().getString(R.string.activity_download_downloading, progress * 100));
        }

    }

}
