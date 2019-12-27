package com.video.test.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.ui.listener.OnDeviceItemClickListener;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Enoch Created on 2018/12/28.
 */
public class CastDeviceAdapter extends RecyclerView.Adapter<CastDeviceAdapter.ViewHolder> {

    List<LelinkServiceInfo> mLelinkSeviceInfo;
    OnDeviceItemClickListener mDeviceListener;


    @NonNull
    @Override
    public CastDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_cast_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastDeviceAdapter.ViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mLelinkSeviceInfo);

    }

    @Override
    public int getItemCount() {
        return null == mLelinkSeviceInfo ? 0 : mLelinkSeviceInfo.size();
    }

    public void setDeviceData(List<LelinkServiceInfo> infos) {
        if (null != infos) {
            if (null == mLelinkSeviceInfo) {
                mLelinkSeviceInfo = new ArrayList<>(infos);
            } else {
                mLelinkSeviceInfo.clear();
                mLelinkSeviceInfo.addAll(infos);
            }
            notifyDataSetChanged();
        }
    }

    public void setDeviceListener(OnDeviceItemClickListener deviceListener) {
        this.mDeviceListener = deviceListener;
    }

    class ViewHolder extends BaseViewHolder<List<LelinkServiceInfo>> {
        int mSelectedPosition = -1;
        private final TextView mDeviceName;
        private final CheckedTextView mCtvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            mDeviceName = itemView.findViewById(R.id.tv_recycle_item_deviceName);
            mCtvStatus = itemView.findViewById(R.id.ctv_recycle_item_deviceName);
        }


        @Override
        public void bindData(int position, int viewType, List<LelinkServiceInfo> lelinkServiceInfos) {
            LelinkServiceInfo info = lelinkServiceInfos.get(position);
            mDeviceName.setText(info.getName());
            mDeviceName.setOnClickListener(v -> {
                        mDeviceListener.onClick(position, info);
                        mSelectedPosition = position;
                        setSelectedPosition(position);
                    }
            );
        }

        private void setSelectedPosition(int position) {
            LogUtils.d("CastAdapter ", "position : " + position + "selectedPosition : " + mSelectedPosition);
            if (position == mSelectedPosition) {
                mCtvStatus.setChecked(true);
            } else {
                mCtvStatus.setChecked(false);
            }
        }
    }

}
