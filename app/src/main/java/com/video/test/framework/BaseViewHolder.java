package com.video.test.framework;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView 的 BaseViewHolder 类
 * Created by Enoch on 2017/5/8.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {


    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /*绑定数据的抽象方法*/
    public abstract void bindData(int position, int viewType, T data);
}
