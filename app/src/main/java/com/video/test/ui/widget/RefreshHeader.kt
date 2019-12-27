package com.video.test.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader

class RefreshHeader : ClassicsHeader {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val lpArrow = mArrowView.layoutParams as LayoutParams
        val lpProgress = mProgressView.layoutParams as LayoutParams
        lpProgress.rightMargin = 0
        lpProgress.leftMargin = 0
        lpArrow.rightMargin = lpProgress.rightMargin
        lpArrow.leftMargin = lpProgress.leftMargin
        lpProgress.removeRule(RelativeLayout.LEFT_OF)
        lpProgress.addRule(RelativeLayout.CENTER_IN_PARENT)
        lpArrow.removeRule(RelativeLayout.LEFT_OF)
        lpArrow.addRule(RelativeLayout.CENTER_IN_PARENT)
        mEnableLastTime = false
        mTitleText.visibility = View.GONE
        mLastUpdateText.visibility = View.GONE
    }
}