package com.video.test.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.video.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 横屏投屏时的设备列表
 *
 * @author : AhhhhDong
 * @date : 2019/5/10 9:45
 */
public class PlayerChooseCastDeviceAdapter extends RecyclerView.Adapter<PlayerChooseCastDeviceAdapter.ViewHolder> {

    private List<LelinkServiceInfo> mLelinkSeviceInfo = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<LelinkServiceInfo> data) {
        this.mLelinkSeviceInfo = data;
    }

    private LelinkServiceInfo getItem(int position) {
        return mLelinkSeviceInfo.get(position);
    }

    public void clearData() {
        mLelinkSeviceInfo.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bean_recycle_item_cast_device, viewGroup, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TextView tvDeviceName = (TextView) viewHolder.itemView;
        LelinkServiceInfo info = getItem(i);
        tvDeviceName.setText(info.getName());
        tvDeviceName.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLelinkSeviceInfo.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LelinkServiceInfo info);
    }
}
