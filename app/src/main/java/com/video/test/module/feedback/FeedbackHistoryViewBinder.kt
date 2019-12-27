package com.video.test.module.feedback

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.constraint.Group
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
import com.video.test.javabean.FeedbackBean
import com.video.test.utils.UnicodeUtils
import me.drakeet.multitype.ItemViewBinder

class FeedbackHistoryViewBinder : ItemViewBinder<FeedbackBean, FeedbackHistoryViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.bean_recycle_item_feedback, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: FeedbackBean) {
        holder.tvFeedTime.text = item.addTime
        holder.tvReplyTime.text = item.replyTime
        holder.tvFeedContent.text = UnicodeUtils.unicodeToString(item.feedContent)
        if (TextUtils.isEmpty(item.replyContent) || TextUtils.isEmpty(item.replyTime)) {
            //未回复
            holder.groupReply.visibility = View.GONE
            val builder = SpannableStringBuilder("${item.tagName} (未回复)")
            builder.setSpan(ForegroundColorSpan(Color.parseColor("#ffad43")), builder.length - 5, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.tvFeedType.text = builder
        } else {
            holder.tvReplyContent.text = UnicodeUtils.unicodeToString(item.replyContent)
            holder.groupReply.visibility = View.VISIBLE
            holder.tvFeedType.text = "${item.tagName} (已回复)"
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFeedType: TextView = itemView.findViewById(R.id.tv_feedback_type)
        val tvFeedTime: TextView = itemView.findViewById(R.id.tv_feedback_time)
        val tvFeedContent: TextView = itemView.findViewById(R.id.tv_feedback_content)
        val tvReplyContent: TextView = itemView.findViewById(R.id.tv_feedback_reply)
        val tvReplyTime: TextView = itemView.findViewById(R.id.tv_feedback_reply_time)
        val groupReply: Group = itemView.findViewById(R.id.group_reply)
    }
}