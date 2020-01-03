package com.video.test.module.ad

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.umeng.analytics.MobclickAgent
import com.video.test.R
import com.video.test.TestApp
import com.video.test.ui.base.BaseActivity
import com.video.test.utils.LogUtils

@Route(path = "/ad/activity")
class AdActivity : BaseActivity<AdPresenter>(), AdContract.View {

    companion object {
        private const val TAG = "AdActivity"
    }

    @BindView(R.id.tv_skip_splash)
    lateinit var mTvSkip: TextView
    @BindView(R.id.iv_ad_splash)
    lateinit var mIvAd: ImageView
    @BindView(R.id.layout_skip)
    lateinit var mLayoutSkip: FrameLayout

    @JvmField
    @Autowired(name = "ad_name")
    var mAdName: String? = null
    @JvmField
    @Autowired(name = "jump_url")
    var mWebUrl: String? = null
    @JvmField
    @Autowired(name = "pic_url")
    var mPicUrl: String? = null
    @JvmField
    @Autowired(name = "ad_id")
    var mAdId: String? = null
    @JvmField
    @Autowired(name = "isSplash")
    var mIsSplash: Boolean = false
    @JvmField
    @Autowired(name = "showTime")
    var mShowTime: Int = 0

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_ad
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        //开始计时
        mPresenter.countDownSplash(this.applicationContext, mShowTime)
        //展示广告图片
        mPicUrl?.let {
            LogUtils.d(TAG, mPicUrl)
            mPresenter.saveAndShowImage(it, mIvAd)
        }
        //点击跳过按钮，跳转到主界面
        mLayoutSkip.setOnClickListener {
            if (mIsSplash)
                ARouter.getInstance().build("/homepage/activity").navigation()
            finish()
        }

        mIvAd.setOnClickListener {
            MobclickAgent.onEvent(TestApp.getContext(), "launch_click_ad", mAdName)
            mPresenter.addAdInfo(mAdId)
            ARouter.getInstance().build("/browser/activity")
                    .withString("mWebUrl", mWebUrl)
                    .navigation()
        }
    }

    override fun getTvSkip(): TextView? {
        return mTvSkip
    }

    override fun getLayoutSkip(): FrameLayout? {
        return mLayoutSkip
    }

    override fun isSplash(): Boolean {
        return mIsSplash
    }

    override fun close() {
        finish()
    }
}