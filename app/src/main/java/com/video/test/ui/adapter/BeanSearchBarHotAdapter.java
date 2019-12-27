package com.video.test.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.javabean.SearchHotWordBean;
import com.video.test.ui.listener.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Enoch Created on 2018/8/3.
 */
public class BeanSearchBarHotAdapter extends RecyclerView.Adapter<BaseViewHolder<List<SearchHotWordBean>>> {


    private ArrayList<SearchHotWordBean> mSearchHotWordList;
    private OnItemClickListener mOnItemClickListener;


    @NonNull
    @Override
    public BaseViewHolder<List<SearchHotWordBean>> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_hot_word, parent, false);
        return new BeanSearchBarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder<List<SearchHotWordBean>> holder, final int position) {
        holder.bindData(position, holder.getItemViewType(), mSearchHotWordList);

        if (holder instanceof BeanSearchBarViewHolder) {
            ((BeanSearchBarViewHolder) holder).tvSearchWord.setOnClickListener(
                    v -> {
                        mOnItemClickListener.onItemClick(((BeanSearchBarViewHolder) holder).tvSearchWord, position);
                        MobclickAgent.onEvent(TestApp.getContext(), "search_hot_word", "点击热词索索");
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return (null == mSearchHotWordList) ? 0 : mSearchHotWordList.size();
    }

    public void setData(List<SearchHotWordBean> searchHistoryWordBeanList) {
        if (null == mSearchHotWordList) {
            mSearchHotWordList = new ArrayList<>();
            mSearchHotWordList.addAll(searchHistoryWordBeanList);
        } else {
            mSearchHotWordList.clear();
            mSearchHotWordList.addAll(searchHistoryWordBeanList);
        }
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    static class BeanSearchBarViewHolder extends BaseViewHolder<List<SearchHotWordBean>> {

        private final ImageButton ivHotRank;
        private final TextView tvSearchWord;

        BeanSearchBarViewHolder(View itemView) {
            super(itemView);
            tvSearchWord = itemView.findViewById(R.id.tv_search_hot_word_recycle);
            ivHotRank = itemView.findViewById(R.id.iv_search_hot_word_rank);
        }

        @Override
        public void bindData(final int position, int viewType, List<SearchHotWordBean> data) {
            if (null != data) {
                final SearchHotWordBean searchHotWordBean = data.get(position);
                tvSearchWord.setText(searchHotWordBean.getVod_keyword());
                if (position == AppConstant.LIST_POSITION_ZERO) {
                    ivHotRank.setImageResource(R.drawable.ic_his_item1);
                    ivHotRank.setVisibility(View.VISIBLE);
                } else if (position == AppConstant.LIST_POSITION_ONE) {
                    ivHotRank.setImageResource(R.drawable.ic_his_item2);
                    ivHotRank.setVisibility(View.VISIBLE);
                } else if (position == AppConstant.LIST_POSITION_TWO) {
                    ivHotRank.setImageResource(R.drawable.ic_his_item3);
                    ivHotRank.setVisibility(View.VISIBLE);
                } else {
                    ivHotRank.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
