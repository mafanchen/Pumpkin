package com.video.test.module.usercenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.javabean.ButtonBean;
import com.video.test.utils.PixelUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author Reus
 * @date 2019/3/5
 * 用户中心页面，vip设置
 */
public class UserCenterVipPermissionViewBinder extends ItemViewBinder<ButtonBean, UserCenterVipPermissionViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycler_item_vip_permission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ButtonBean item) {
        //根据位置设置间距
        MultiTypeAdapter adapter = getAdapter();
        int itemCount = adapter.getItemCount();
        int position = adapter.getItems().indexOf(item);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.itemView.getLayoutParams());
        int left = PixelUtils.dp2px(holder.itemView.getContext(), 4.5f);
        int right = left;
        if (position == 0) {
            left = 0;
        } else if (position == itemCount - 1) {
            right = 0;
        }
        params.setMargins(left, 0, right, 0);
        holder.itemView.setLayoutParams(params);

//        int imageId = item.getImageId();
//        String name = item.getName();
//        holder.tvName.setText(name);
//        holder.tvName.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);

        int imageId = item.getImageId();
        holder.itemView.setBackgroundResource(imageId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_permission_name)
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
