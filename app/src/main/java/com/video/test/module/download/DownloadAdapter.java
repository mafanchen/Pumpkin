package com.video.test.module.download;

import android.annotation.SuppressLint;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.db.DBManager;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author Enoch Created on 2019/1/18.
 */
public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DownloadAdapter";

    private List<Object> mList;
    private DownloadItemClickListener mDownloadListener;
    private boolean mIsManager = false;

    static final int TYPE_TITLE = 1;
    static final int TYPE_TASK = 2;

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) instanceof String ? TYPE_TITLE : TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == TYPE_TASK) {
            View view = inflater.inflate(R.layout.bean_recycle_item_download, viewGroup, false);
            return new TaskViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.bean_recycle_item_download_group_title, viewGroup, false);
            return new TitleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bindData(position, (M3U8DownloadBean) mList.get(position), mDownloadListener, mIsManager);
        } else if (holder instanceof TitleViewHolder) {
            if (position == 0) {
                ((TitleViewHolder) holder).divider.setVisibility(View.GONE);
            } else {
                ((TitleViewHolder) holder).divider.setVisibility(View.VISIBLE);
            }
            ((TitleViewHolder) holder).tvTitle.setText(((String) mList.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        LogUtils.d(TAG, "onViewAttachedToWindow");
        if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).onAttach();
        }
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).onDetach();
        }
        LogUtils.d(TAG, "onViewDetachedFromWindow");
    }

    public void setData(List<Object> m3u8List) {
        if (null == mList) {
            mList = new ArrayList<>(m3u8List);
        } else {
            mList.clear();
            mList.addAll(m3u8List);
        }
        notifyDataSetChanged();
    }

    void setClickListener(DownloadItemClickListener downloadItemClickListener) {
        this.mDownloadListener = downloadItemClickListener;
    }

    void setIsManager(boolean isManager) {
        mIsManager = isManager;
    }

    void deSelectAll() {
        if (mList != null) {
            for (Object o : mList) {
                if (o instanceof M3U8DownloadBean) {
                    ((M3U8DownloadBean) o).setSelect(false);
                }
            }
        }
    }

    public void selectAll() {
        if (mList != null) {
            for (Object o : mList) {
                if (o instanceof M3U8DownloadBean) {
                    ((M3U8DownloadBean) o).setSelect(true);
                }
            }
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.divider)
        View divider;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIvPic;
        private final TextView mTvVideoName;
        private final ProgressBar mProgressbar;
        private final TextView mTvTaskStatus;
        private final CheckBox mCheckBoxSelect;
        private M3U8DownloadBean m3U8Bean;
        private Disposable disposable;


        public TaskViewHolder(View itemView) {
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
            this.m3U8Bean = m3u8;
            mCheckBoxSelect.setOnCheckedChangeListener(null);
            mCheckBoxSelect.setChecked(m3u8.isSelect());
            mCheckBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        m3u8.setSelect(isChecked);
                        listener.onItemSelected(isChecked, m3u8);
                    }
            );
            mTvVideoName.setText(m3u8.getVideoName());
            setProgress(m3u8);
            setTaskStatus(m3u8);
            mIvPic.setOnClickListener(v -> {
                if (isManager) {
                    return;
                }
                LogUtils.d(TAG, "mCtvStartPause click");
                if (null != listener) {
                    switch (m3U8Bean.getTaskStatus()) {
                        case AppConstant.M3U8_TASK_PENDING:
                        case AppConstant.M3U8_TASK_PREPARE:
                        case AppConstant.M3U8_TASK_DOWNLOADING:
                            listener.pauseTask(m3u8, position);
                            mTvTaskStatus.setText(R.string.activity_download_pause);
                            showImage(R.drawable.ic_download_paused);
                            break;
                        case AppConstant.M3U8_TASK_SUCCESS:
                            listener.playLocalVideo(m3u8.getM3u8FilePath(), m3u8.getVideoName());
                            break;
                        case AppConstant.M3U8_TASK_PAUSE:
                        case AppConstant.M3U8_TASK_ERROR:
                        case AppConstant.M3U8_TASK_ENOSPC:
                            listener.startTask(m3u8, position);
                            showImage(R.drawable.ic_download_downloading);
                            setProgress(m3U8Bean);
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
                        listener.playLocalVideo(m3u8.getM3u8FilePath(), m3u8.getVideoName());
                    } else {
                        LogUtils.d(TAG, "videoName : " + m3u8.getVideoName() + " videoUrl : " + m3u8.getVideoUrl());
                        listener.playNetworkVideo(m3u8.getVideoId(), m3u8.getVideoUrl());
                    }
                }
            });
        }

        void onAttach() {
            disposable = Observable.interval(0, 1500, TimeUnit.MILLISECONDS)
                    .map(new Function<Long, M3U8DownloadBean>() {
                        @Override
                        public M3U8DownloadBean apply(Long aLong) throws Exception {
                            return DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(m3U8Bean.getVideoUrl());
                        }
                    }).compose(RxSchedulers.io_main())
                    .subscribe(new Consumer<M3U8DownloadBean>() {
                        @Override
                        public void accept(M3U8DownloadBean m3U8DownloadBean) throws Exception {
                            TaskViewHolder.this.m3U8Bean = m3U8DownloadBean;
                            LogUtils.d(TAG, "onAttach success urls : " + m3U8DownloadBean.getVideoUrl());
                            setProgress(m3U8DownloadBean);
                            setTaskStatus(m3U8DownloadBean);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e(TAG, "onAttach error : " + throwable.getMessage());
                        }
                    });
        }

        void setTaskStatus(M3U8DownloadBean m3u8) {
            switch (m3u8.getTaskStatus()) {
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

        @SuppressLint("StringFormatInvalid")
        void setProgress(M3U8DownloadBean m3U8DownloadBean) {
            if (null != mProgressbar && null != mTvTaskStatus) {
                LogUtils.d(TAG, "progress Cur : " + m3U8DownloadBean.getCurTs() + " total : " + m3U8DownloadBean.getTotalTs());
                mProgressbar.setMax(m3U8DownloadBean.getTotalTs());
                if (m3U8DownloadBean.isDownloaded()) {
                    mProgressbar.setProgress(m3U8DownloadBean.getTotalTs());
                } else {
                    mProgressbar.setProgress(m3U8DownloadBean.getCurTs());
                    mTvTaskStatus.setText(itemView.getContext().getString(R.string.activity_download_downloading,
                            ((float) mProgressbar.getProgress()) * 100 / mProgressbar.getMax()));
                }

            }
        }

        void onDetach() {
            if (null != disposable && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

}
