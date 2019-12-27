package com.video.test.module.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.video.test.R
import com.video.test.javabean.SearchNoMoreDataBean
import me.drakeet.multitype.ItemViewBinder

class SearchNoMoreDataViewBinder : ItemViewBinder<SearchNoMoreDataBean, SearchNoMoreDataViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.bean_recycle_item_search_no_more_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SearchNoMoreDataBean) {
        holder.textView.text = item.text
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_no_more_data)
    }
}