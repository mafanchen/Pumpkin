package com.video.test.ui.viewbinder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.video.test.R;
import com.video.test.javabean.HeaderViewBean;
import com.video.test.ui.listener.RecycleItemListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author Enoch Created on 2018/9/19.
 * 暂未启用
 */
public class HeaderViewBinder extends ItemViewBinder<HeaderViewBean, HeaderViewBinder.ViewHolder> {
    private static final String TAG = "HeaderViewBinder";
    private RecycleItemListener mRecycleItemListener;


    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_item_header, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull HeaderViewBean item) {
        if (null != holder.mIvInvitation) {
            holder.mIvInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecycleItemListener.onItemClick();
                }
            });

        }
    }

    public void setRecycleItemListener(RecycleItemListener recycleItemListener) {
        this.mRecycleItemListener = recycleItemListener;
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_invitation_recommend_fragment)
        ImageView mIvInvitation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
