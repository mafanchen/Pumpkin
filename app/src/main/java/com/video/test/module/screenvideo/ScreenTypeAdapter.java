package com.video.test.module.screenvideo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.ScreenTypeBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class ScreenTypeAdapter extends RecyclerView.Adapter<ScreenTypeAdapter.ScreenMovieViewHolder> {
    private static final String TAG = "ScreenTypeAdapter";
    private List<ScreenTypeBean> mScreenTypeBeans;
    private int mSelectedPos = -1;
    ScreenItemListener mScreenItemListener;


    @NonNull
    @Override
    public ScreenMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_screen_video, parent, false);
        return new ScreenMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenMovieViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mScreenTypeBeans);
    }


    @Override
    public void onBindViewHolder(@NonNull ScreenMovieViewHolder holder, int position, @NonNull List<Object> payloads) {
        CheckedTextView mCheckBox = holder.mCheckBox;
        ScreenTypeBean screenTypeBean = mScreenTypeBeans.get(position);
        if (payloads.isEmpty()) {
            mCheckBox.setText(screenTypeBean.getType_key());
            mCheckBox.setChecked(mSelectedPos == position);
        } else {
            mCheckBox.setChecked(mSelectedPos == position);
        }


        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPos != position) {
                    mCheckBox.setChecked(true);
                    if (mSelectedPos != -1) {
                        notifyItemChanged(mSelectedPos, 0);
                    }
                }
                mSelectedPos = position;
                mScreenItemListener.setItem(screenTypeBean.getType_key(), screenTypeBean.getType_val());
                LogUtils.d(TAG, "selected position == " + position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == mScreenTypeBeans ? 0 : mScreenTypeBeans.size();
    }

    public void setData(List<ScreenTypeBean> screenYearBeans) {
        if (null == mScreenTypeBeans) {
            mScreenTypeBeans = new ArrayList<>();
            mScreenTypeBeans.addAll(screenYearBeans);
        } else {
            mScreenTypeBeans.clear();
            mScreenTypeBeans.addAll(screenYearBeans);
        }
        notifyDataSetChanged();
    }

    void setSelectedPosition(int positon) {
        this.mSelectedPos = positon;
    }

    void setScreenItemListener(ScreenItemListener screenItemListener) {
        this.mScreenItemListener = screenItemListener;
    }

    static class ScreenMovieViewHolder extends BaseViewHolder<List<ScreenTypeBean>> {

        @BindView(R.id.cb_recycle_item_screen)
        CheckedTextView mCheckBox;

        public ScreenMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<ScreenTypeBean> data) {

        }
    }
}
