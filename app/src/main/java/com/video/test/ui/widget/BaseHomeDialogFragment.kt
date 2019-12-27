package com.video.test.ui.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.video.test.R
import com.video.test.javabean.HomeDialogBean
import com.video.test.utils.PixelUtils

abstract class BaseHomeDialogFragment : DialogFragment() {

    val TAG = "BaseHomeDialogFragment"
    @JvmField
    protected var mDialogBean: HomeDialogBean? = null
    @JvmField
    protected var mIvClose: ImageView? = null
    @JvmField
    protected var mDialogItemClickListener: DialogItemClickListener? = null
    @JvmField
    protected var mDialogDismissListener: DialogDismissListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.update_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        mIvClose?.setOnClickListener {
            dismiss()
            mDialogDismissListener?.onDismiss(this)
        }
    }

    override fun onStart() {
        super.onStart()
        //点击window外的区域 是否消失
        dialog.setCanceledOnTouchOutside(false)
        val dialogWindow = dialog.window
        dialogWindow!!.setGravity(Gravity.TOP)
        val lp = dialogWindow.attributes
        lp.y = PixelUtils.dp2px(dialogWindow.context, 150f)
        dialogWindow.attributes = lp
    }

    override fun show(manager: FragmentManager, tag: String) {
        try {
            super.show(manager, tag)

        } catch (e: IllegalStateException) {
            Log.d(TAG, "show state error: " + e.message)
        }

    }

    fun setDialogItemClickListener(listener: DialogItemClickListener) {
        mDialogItemClickListener = listener
    }

    fun setDialogDismissListener(listener: DialogDismissListener) {
        mDialogDismissListener = listener
    }

    protected abstract fun initView(view: View)

    protected abstract fun getLayoutId(): Int

    interface DialogItemClickListener {
        fun onClick(bean: HomeDialogBean, dialog: DialogFragment)
    }

    interface DialogDismissListener {
        fun onDismiss(fragment: BaseHomeDialogFragment)
    }

}