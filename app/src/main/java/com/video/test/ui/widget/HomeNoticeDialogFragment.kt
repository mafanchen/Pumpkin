package com.video.test.ui.widget

import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import com.video.test.R
import com.video.test.javabean.HomeDialogBean

class HomeNoticeDialogFragment : BaseHomeDialogFragment() {

    private var mTvConfirm: TextView? = null
    private var mTvTitle: TextView? = null
    private var mTvContent: TextView? = null

    companion object {
        fun newInstance(bean: HomeDialogBean): HomeNoticeDialogFragment {
            val fragment = HomeNoticeDialogFragment()
            fragment.mDialogBean = bean
            return fragment
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.bean_dialog_home_notice
    }

    override fun initView(view: View) {
        mIvClose = view.findViewById(R.id.iv_close_home_dialog)
        mTvTitle = view.findViewById(R.id.tv_title)
        mTvContent = view.findViewById(R.id.tv_content)
        mTvConfirm = view.findViewById(R.id.tv_confirm)
        if (mDialogBean != null) {
            mTvTitle?.text = mDialogBean!!.title
            mTvContent?.text = mDialogBean!!.content
            mTvContent?.movementMethod = ScrollingMovementMethod.getInstance()
            if (TextUtils.isEmpty(mDialogBean!!.button)) {
                mTvConfirm?.visibility = View.GONE
            } else {
                mTvConfirm?.text = mDialogBean!!.button
                mTvConfirm?.setOnClickListener { mDialogItemClickListener?.onClick(mDialogBean!!, this) }
            }
        }
    }
}