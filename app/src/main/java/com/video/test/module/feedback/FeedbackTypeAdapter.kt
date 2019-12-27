package com.video.test.module.feedback

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.video.test.R
import com.video.test.javabean.FeedbackTypeBean

class FeedbackTypeAdapter(private val onChooseListener: OnChooseListener) : RecyclerView.Adapter<FeedbackTypeAdapter.ViewHolder>() {

    var list: List<FeedbackTypeBean>? = null

    private var currentPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bean_recycle_item_feedback_type, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = if (list != null) list!!.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!![position]
        if (holder.itemView is CheckBox) {
            holder.itemView.text = item.name
            holder.itemView.isChecked = currentPosition == position
            holder.itemView.setOnClickListener {
                currentPosition = holder.adapterPosition
                onChooseListener.onChoose(item)
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnChooseListener {
        fun onChoose(type: FeedbackTypeBean)
    }
}