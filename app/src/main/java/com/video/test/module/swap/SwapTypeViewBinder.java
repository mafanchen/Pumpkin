package com.video.test.module.swap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.ShareExchangeListBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

public class SwapTypeViewBinder extends ItemViewBinder<ShareExchangeListBean.ShareExchangeBean, SwapTypeViewBinder.ViewHolder> {

    /**
     * 可用兑换人数
     */
    private int shareNum = 100;

    private OnSwapClickListener onSwapClickListener;

    public void setOnSwapClickListener(OnSwapClickListener onSwapClickListener) {
        this.onSwapClickListener = onSwapClickListener;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycler_item_swap, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ShareExchangeListBean.ShareExchangeBean item) {
        holder.tvName.setText(item.getShareName());
        if (item.getShareSetPic() != null) {
            GlideApp.with(holder.itemView.getContext())
                    .load(item.getShareSetPic())
                    .centerCrop()
                    .error(R.drawable.ic_swap_crown)
                    .placeholder(R.drawable.ic_swap_crown)
                    .into(holder.ivVip);
        } else {
            GlideApp.with(holder.itemView.getContext()).load(R.drawable.ic_swap_crown).into(holder.ivVip);
        }
        holder.tvCount.setText(holder.itemView.getContext().getString(R.string.activity_swap_count, item.getShareSetNum()));
        holder.tvTime.setText(holder.itemView.getContext().getString(R.string.activity_swap_time, item.getShareSetTime()));
        holder.btnSwap.setEnabled(shareNum >= item.getShareSetNum());
        holder.btnSwap.setTag(item);
        holder.btnSwap.setOnClickListener(v -> {
            ShareExchangeListBean.ShareExchangeBean temp = (ShareExchangeListBean.ShareExchangeBean) v.getTag();
            if (temp != null && onSwapClickListener != null) {
                onSwapClickListener.onClick(temp);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_vip)
        ImageView ivVip;
        @BindView(R.id.tv_vip_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.btn_swap)
        TextView btnSwap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnSwapClickListener {
        void onClick(ShareExchangeListBean.ShareExchangeBean bean);
    }
}
