package com.video.test.ui.viewbinder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.test.R;
import com.video.test.javabean.FooterViewBean;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author Enoch Created on 2018/9/19.
 *
 */
public class FooterViewBinder extends ItemViewBinder<FooterViewBean,FooterViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_item_footer, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FooterViewBean item) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
