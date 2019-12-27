package com.video.test.module.screenvideo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.ScreenAreaBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class ScreenAreaAdapter extends RecyclerView.Adapter<ScreenAreaAdapter.ScreenMovieViewHolder> {
    private static final String TAG = "ScreenAreaAdapter";
    private List<ScreenAreaBean> mScreenAreaBeans;
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
        ScreenAreaBean screenAreaBean = mScreenAreaBeans.get(position);
        if (payloads.isEmpty()) {
            holder.mCheckBox.setText(screenAreaBean.getArea_key());
            holder.mCheckBox.setChecked(mSelectedPos == position);
        } else {
            holder.mCheckBox.setChecked(mSelectedPos == position);
        }

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPos != position) {
                    holder.mCheckBox.setChecked(true);
                    if (mSelectedPos != -1) {
                        notifyItemChanged(mSelectedPos, 0);

                    }
                }
                mSelectedPos = position;
                mScreenItemListener.setItem(screenAreaBean.getArea_key(), screenAreaBean.getArea_val());
                LogUtils.d(TAG, "selected position == " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mScreenAreaBeans ? 0 : mScreenAreaBeans.size();
    }

    public void setData(List<ScreenAreaBean> screenYearBeans) {
        if (null == mScreenAreaBeans) {
            mScreenAreaBeans = new ArrayList<>();
            mScreenAreaBeans.addAll(screenYearBeans);
        } else {
            mScreenAreaBeans.clear();
            mScreenAreaBeans.addAll(screenYearBeans);
        }
        notifyDataSetChanged();
    }


    void setSelectedPosition(int position) {
        this.mSelectedPos = position;

    }

    void setScreenItemListener(ScreenItemListener screenItemListener) {
        this.mScreenItemListener = screenItemListener;
    }

    static class ScreenMovieViewHolder extends BaseViewHolder<List<ScreenAreaBean>> {

        @BindView(R.id.cb_recycle_item_screen)
        CheckedTextView mCheckBox;

        public ScreenMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<ScreenAreaBean> data) {

        }
    }
}
