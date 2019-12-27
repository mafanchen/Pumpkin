package com.video.test.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.SearchHistoryWordBean;
import com.video.test.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Enoch Created on 2018/8/3.
 */
public class BeanSearchBarHistoryAdapter extends RecyclerView.Adapter<BaseViewHolder<List<SearchHistoryWordBean>>> {


    private ArrayList<SearchHistoryWordBean> mSearchHistoryList;
    private OnItemClickListener mOnItemClickListener;


    @NonNull
    @Override
    public BaseViewHolder<List<SearchHistoryWordBean>> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_history_word, parent, false);
        return new BeanSearchBarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder<List<SearchHistoryWordBean>> holder, final int position) {
        holder.bindData(position, holder.getItemViewType(), mSearchHistoryList);

        if (holder instanceof BeanSearchBarViewHolder) {
            ((BeanSearchBarViewHolder) holder).tvHistoryWord.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onItemClick(((BeanSearchBarViewHolder) holder).tvHistoryWord, position);

                        }
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return (null == mSearchHistoryList) ? 0 : mSearchHistoryList.size();
    }


    public void setData(List<SearchHistoryWordBean> searchHistoryWordBeanList) {
        if (null == mSearchHistoryList) {
            mSearchHistoryList = new ArrayList<>();
            mSearchHistoryList.addAll(searchHistoryWordBeanList);
        } else {
            mSearchHistoryList.clear();
            mSearchHistoryList.addAll(searchHistoryWordBeanList);
        }
        notifyDataSetChanged();
    }

    public List<SearchHistoryWordBean> getData() {
        return mSearchHistoryList;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    static class BeanSearchBarViewHolder extends BaseViewHolder<List<SearchHistoryWordBean>> {

        TextView tvHistoryWord;

        BeanSearchBarViewHolder(View itemView) {
            super(itemView);
            tvHistoryWord = itemView.findViewById(R.id.tv_search_history_word_recycle);
        }

        @Override
        public void bindData(final int position, int viewType, List<SearchHistoryWordBean> data) {
            if (null != data) {
                final SearchHistoryWordBean searchHistoryWordBean = data.get(position);
                tvHistoryWord.setText(searchHistoryWordBean.getKeyword());
            }
        }
    }
}
