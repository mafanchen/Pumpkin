package com.video.test.ui.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.video.test.R

class SearchAssociationAdapter : RecyclerView.Adapter<SearchAssociationAdapter.ViewHolder>() {

    var searchWord: String? = null
    val data: MutableList<String> = ArrayList()
    var onItemClickListener: OnItemClickListener? = null

    fun setData(data: List<String>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bean_recycle_item_association, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val associationWord = data[position]
        val tvAssociation = holder.itemView as TextView
        if (TextUtils.isEmpty(searchWord)) {
            tvAssociation.text = associationWord
        } else {
            val start: Int = associationWord.indexOf(searchWord!!)
            if (start == -1) {
                tvAssociation.text = associationWord
            } else {
                val builder = SpannableStringBuilder(associationWord)
                val span = ForegroundColorSpan(Color.parseColor("#38dca1"))
                builder.setSpan(span, start, start + searchWord!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvAssociation.text = builder
            }
        }
        tvAssociation.setOnClickListener { onItemClickListener?.onItemClick(associationWord) }
    }

    interface OnItemClickListener {
        fun onItemClick(word: String)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
