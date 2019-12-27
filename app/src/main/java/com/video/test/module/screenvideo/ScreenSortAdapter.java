package com.video.test.module.screenvideo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.ScreenSortBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class ScreenSortAdapter extends RecyclerView.Adapter<ScreenSortAdapter.ScreenMovieViewHolder> {
    private static final String TAG = "ScreenSortAdapter";
    private List<ScreenSortBean> mScreenSortBeans;
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

    }

    @Override
    public void onBindViewHolder(@NonNull ScreenMovieViewHolder holder, int position, @NonNull List<Object> payloads) {
        ScreenSortBean screenSortBean = mScreenSortBeans.get(position);
        CheckedTextView mCheckBox = holder.mCheckBox;
        if (payloads.isEmpty()) {
            mCheckBox.setText(screenSortBean.getPlay_key());
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
                mScreenItemListener.setItem(screenSortBean.getPlay_key(), screenSortBean.getPlay_val());
                LogUtils.d(TAG, "selected position == " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mScreenSortBeans ? 0 : mScreenSortBeans.size();
    }


    public void setData(List<ScreenSortBean> screenYearBeans) {
        if (null == mScreenSortBeans) {
            mScreenSortBeans = new ArrayList<>();
            mScreenSortBeans.addAll(screenYearBeans);
        } else {
            mScreenSortBeans.clear();
            mScreenSortBeans.addAll(screenYearBeans);
        }
        notifyDataSetChanged();
    }

    void setSelectedPosition(int positon) {
        this.mSelectedPos = positon;
    }

    void setScreenItemListener(ScreenItemListener screenItemListener) {
        this.mScreenItemListener = screenItemListener;
    }

    static class ScreenMovieViewHolder extends BaseViewHolder<List<ScreenSortBean>> {

        @BindView(R.id.cb_recycle_item_screen)
        CheckedTextView mCheckBox;

        public ScreenMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<ScreenSortBean> data) {

        }
    }
}
