package com.video.test.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.video.test.R
import com.youth.banner.Banner


class Banner : Banner {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun start(): Banner {
        val start = super.start()
        val titleView: View = findViewById(R.id.titleView)
        titleView.background = ContextCompat.getDrawable(context, R.drawable.shape_bg_banner_title)
        return start
    }

}