package com.video.test.module.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hpplay.gson.Gson;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.HistoryListBean;
import com.video.test.ui.adapter.BaseSelectableAdapter;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/7.
 */
public class HistoryAdapter extends BaseSelectableAdapter<HistoryListBean, HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = "HistoryAdapter";
    private boolean mIsShow;
    private List<HistoryListBean> mHistoryBeanList;


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_history_video, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mHistoryBeanList);
        if (mIsShow) {
            holder.mGroupHistory.setVisibility(View.VISIBLE);
            LogUtils.d(TAG, "onBindViewHolder True mGroup ==" + holder.mCheckBox.getVisibility());
        } else {
            holder.mGroupHistory.setVisibility(View.GONE);
            LogUtils.d(TAG, "onBindViewHolder True mGroup ==" + holder.mCheckBox.getVisibility());
        }
    }

    @Override
    public int getItemCount() {
        return null == mHistoryBeanList ? 0 : mHistoryBeanList.size();
    }

    public void setData(List<HistoryListBean> historyListBeans) {
        if (null == mHistoryBeanList) {
            mHistoryBeanList = new ArrayList<>();
            mHistoryBeanList.addAll(historyListBeans);
        } else {
            mHistoryBeanList.clear();
            mHistoryBeanList.addAll(historyListBeans);
        }
        notifyDataSetChanged();
    }

    public void addData(List<HistoryListBean> historyListBeans) {
        if (null != mHistoryBeanList) {
            mHistoryBeanList.addAll(historyListBeans);
        }
        notifyDataSetChanged();
    }


    public void changeGroupVisibility(boolean isShow) {
        this.mIsShow = isShow;
        notifyDataSetChanged();
    }

    @Override
    protected List<HistoryListBean> getList() {
        return mHistoryBeanList;
    }

    public String getSelectItem() {
        List<String> selectItem = new ArrayList<>();
        for (HistoryListBean historyListBean : mHistoryBeanList) {
            if (historyListBean.isSelected()) {
                selectItem.add(historyListBean.getId());
            }
        }
        Gson gson = new Gson();
        return gson.toJson(selectItem);
    }

    static class HistoryViewHolder extends BaseViewHolder<List<HistoryListBean>> {

        private final Context context;
        @BindView(R.id.iv_recycle_item_videoPic_history)
        ImageView mIvVideoPic;
        @BindView(R.id.cb_recycle_item_history)
        CheckBox mCheckBox;
        @BindView(R.id.tv_recycle_item_videoName_history)
        TextView mTvVideoName;
        @BindView(R.id.tv_recycle_item_videoPercent_history)
        TextView mTvVideoPercent;
        @BindView(R.id.view_background_videoPic_history)
        View mViewPicBackground;
        @BindView(R.id.ll_recycle_item_video_module)
        ConstraintLayout mCl;
        @BindView(R.id.group_recycle_item_history)
        Group mGroupHistory;


        HistoryViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<HistoryListBean> data) {
            final HistoryListBean historyListBean = data.get(position);
            GlideApp.with(context).load(historyListBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvVideoPic);
            mTvVideoName.setText(historyListBean.getVod_name());
            String time = getTimeString(historyListBean.getNowtime()) + "/" + getTimeString(historyListBean.getTotaltime());
            mTvVideoPercent.setText(time);
            mCheckBox.setChecked(historyListBean.isSelected());

            mCl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "mll vodId == " + historyListBean.getVod_id() +
                            " videoName = " + historyListBean.getVod_name());
                    ARouter.getInstance().build("/player/activity")
                            .withString("vodId", historyListBean.getVod_id())
                            .withString("videoUrl", historyListBean.getPlay_url())
//                            .withString("videoTime", historyListBean.getNowtime())
                            // TODO: 2019/4/11 统一播放进度，暂时用播放进度来跳转进度
                            .withString("videoTime", historyListBean.getPlay_degree())
                            .navigation();
                }
            });


            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historyListBean.setSelected(mCheckBox.isChecked());
                    LogUtils.d(TAG, "CheckBox == " + historyListBean.toString());
                }
            });


            mViewPicBackground.setOnClickListener(v -> {
                mCheckBox.setChecked(!mCheckBox.isChecked());
                historyListBean.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "PicBackground == " + historyListBean.toString());
            });
        }

        private String getTimeString(String intTime) {
            if (TextUtils.isEmpty(intTime)) {
                return "00:00:00";
            }
            int time = Integer.parseInt(intTime) / 1000;
            int second = time % 60;
            int minute = time / 60 % 60;
            int hour = time / 60 / 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
        }
    }

}
