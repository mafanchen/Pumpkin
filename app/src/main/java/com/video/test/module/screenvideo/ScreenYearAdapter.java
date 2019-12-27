package com.video.test.module.screenvideo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.ScreenYearBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class ScreenYearAdapter extends RecyclerView.Adapter<ScreenYearAdapter.ScreenMovieViewHolder> {
    private static final String TAG = "ScreenYearAdapter";
    private List<ScreenYearBean> mScreenMovieBeans;
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
        holder.bindData(position, holder.getItemViewType(), mScreenMovieBeans);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenMovieViewHolder holder, int position, @NonNull List<Object> payloads) {
        CheckedTextView mCheckBox = holder.mCheckBox;
        ScreenYearBean screenYearBean = mScreenMovieBeans.get(position);
        if (payloads.isEmpty()) {
            mCheckBox.setText(screenYearBean.getYear_key());
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
                mScreenItemListener.setItem(screenYearBean.getYear_key(), screenYearBean.getYear_val());
                LogUtils.d(TAG, "selected position == " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mScreenMovieBeans ? 0 : mScreenMovieBeans.size();
    }

    public void setData(List<ScreenYearBean> screenYearBeans) {
        if (null == mScreenMovieBeans) {
            mScreenMovieBeans = new ArrayList<>();
            mScreenMovieBeans.addAll(screenYearBeans);
        } else {
            mScreenMovieBeans.clear();
            mScreenMovieBeans.addAll(screenYearBeans);
        }
        notifyDataSetChanged();
    }

    void setSelectedPosition(int positon) {
        this.mSelectedPos = positon;
    }

    void setScreenItemListener(ScreenItemListener screenItemListener) {
        this.mScreenItemListener = screenItemListener;
    }

    static class ScreenMovieViewHolder extends BaseViewHolder<List<ScreenYearBean>> {

        @BindView(R.id.cb_recycle_item_screen)
        CheckedTextView mCheckBox;

        public ScreenMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<ScreenYearBean> data) {

        }
    }
}
