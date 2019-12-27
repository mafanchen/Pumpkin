package com.video.test.module.solve

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.alibaba.android.arouter.facade.annotation.Route
import com.video.test.R

@Route(path = "/solve/activity")
class SolveActivity : AppCompatActivity() {

    @JvmField
    @BindView(R.id.tv_title_toolbar)
    var mTvTitle: TextView? = null
    @JvmField
    @BindView(R.id.ib_back_toolbar)
    var mIbBack: ImageButton? = null

    private var unBinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bean_activity_solve)
        unBinder = ButterKnife.bind(this)
        initToolBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatueBarColor()
        }
    }

    @OnClick(R.id.ib_back_toolbar, R.id.tv_jump_setting)
    fun onViewClick(view: View) {
        when (view.id) {
            R.id.ib_back_toolbar -> finish()
            R.id.tv_jump_setting -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    private fun initToolBar() {
        mTvTitle?.text = "解决方案"
        mIbBack?.visibility = View.VISIBLE
    }

    private fun setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.nav_background)
            window.navigationBarColor = resources.getColor(R.color.navigation_background)
            val contentView = this.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val childView = contentView.getChildAt(0)
            if (null != childView) {
                ViewCompat.setFitsSystemWindows(childView, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder?.unbind()
    }
}