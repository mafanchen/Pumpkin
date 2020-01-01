package com.video.test.module.videotype

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.umeng.analytics.MobclickAgent
import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.javabean.*
import com.video.test.javabean.base.IPageJumpBean
import com.video.test.javabean.event.HotSearchWordRetryEvent
import com.video.test.module.videorecommend.VideoRecommendHorizontalViewBinder
import com.video.test.module.videorecommend.VideoRecommendViewBinder
import com.video.test.ui.base.BaseLazyFragment
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.viewbinder.VideoAdItemViewBinder
import com.video.test.ui.viewbinder.VideoTitleItemViewBinder
import com.video.test.ui.widget.*
import com.video.test.utils.IntentUtils
import com.video.test.utils.LogUtils
import com.video.test.utils.PixelUtils
import com.youth.banner.BannerConfig
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import org.greenrobot.eventbus.EventBus


/**
 * 首页视频各个视频分类列表
 * @author : AhhhhDong
 * @date : 2019/4/9 16:12
 */
abstract class BaseVideoTypeListFragment<P : BaseVideoTypeListPresenter<*, *>> : BaseLazyFragment<P>(), VideoTypeListContract.View {

    companion object {
        const val SPAN_COUNT = 6
    }

    var TAG: String = "VideoTypeListFragment"
    @JvmField
    protected var mAdapter: MultiTypeAdapter? = null
    @JvmField
    protected var mSwipeRefresh: SmartRefreshLayout? = null
    @JvmField
    protected var mRvVideoList: RecyclerView? = null
    @JvmField
    protected var mLoadingView: LoadingView? = null
    @JvmField
    protected var mBanner: Banner? = null

    var pid: Int = 0

    var adType: Int = 0

    private var mIsLoadData = false

    override fun loadData() {
        if (!mIsLoadData) {
            mIsLoadData = true
            initSwipeRefresh()
            mSwipeRefresh?.autoRefresh()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewBeforeLoadData()
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            val pid = savedInstanceState["pid"]
            if (pid is Int) {
                this.pid = pid
            }
            val adType = savedInstanceState["adType"]
            if (adType is Int) {
                this.adType = adType
            }
        }
        mLoadingView?.setOnLoadingListener(object : LoadingView.OnLoadingListener {
            override fun onRetry() {
                mLoadingView?.showContent()
                onRefresh()
                EventBus.getDefault().post(HotSearchWordRetryEvent())
            }

            override fun onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation()
            }
        })
    }

    protected abstract fun initViewBeforeLoadData()

    override fun setVideoData(items: Items) {
        mAdapter?.items = items
        mAdapter?.notifyDataSetChanged()
    }

    override fun setAdapter() {
        val leftRight = PixelUtils.dp2px(mContext, 3f)
        val gridLayoutManager = GridLayoutManager(context, SPAN_COUNT)
        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                val list = mAdapter?.items
                val item = list?.get(position)
                return if (item is VideoTitleBean || item is FooterViewBean || item is AdInfoBean)
                    SPAN_COUNT
                else
                    if (item is VideoRecommendBean) 3 else 2
            }
        }
        gridLayoutManager.spanSizeLookup = spanSizeLookup
        mRvVideoList?.layoutManager = gridLayoutManager
        mRvVideoList?.isNestedScrollingEnabled = true
        mRvVideoList?.setHasFixedSize(true)
        mRvVideoList?.addItemDecoration(DividerItemDecoration(leftRight, leftRight, Color.WHITE))
        mRvVideoList?.addOnScrollListener(GlideOnScrollListener())
        initAdapter()
        mRvVideoList?.adapter = mAdapter
    }

    protected open fun initAdapter() {
        if (mAdapter == null) {
            mAdapter = MultiTypeAdapter()
            mAdapter!!.register(VideoTitleBean::class.java, VideoTitleItemViewBinder())
            val videoAdItemViewBinder = VideoAdItemViewBinder()
            videoAdItemViewBinder.setOnAdClickListener { mPresenter.addAdInfo(adType, it) }
            mAdapter!!.register(AdInfoBean::class.java, videoAdItemViewBinder)
            mAdapter!!.register(VideoRecommendBean::class.java, VideoRecommendHorizontalViewBinder())
            mAdapter!!.register(VideoBean::class.java, VideoRecommendViewBinder())
            mAdapter!!.register(FooterViewBean::class.java, FooterViewBinder())
        }
    }

    override fun showRefreshLayout() {
        mSwipeRefresh?.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mSwipeRefresh?.finishRefresh(isSuccess)
    }

    protected open fun initSwipeRefresh() {
        mSwipeRefresh?.setEnableLoadMore(false)
        mSwipeRefresh?.setRefreshHeader(RefreshHeader(mContext))
        mSwipeRefresh?.setOnRefreshListener { onRefresh() }
    }

    protected open fun onRefresh() {
        mPresenter.getHomePageVideoList(pid)
        mPresenter.getBannerAndNotice(pid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //这里如果不销毁，adapter会纪录位置，导致页面切换销毁重建时的位置不在顶部
        mAdapter = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pid", pid)
        outState.putInt("adType", adType)
    }

    override fun showNetworkErrorView() {
        mLoadingView?.showError()
    }


    @Suppress("UNCHECKED_CAST")
    override fun initBanner(bannerList: List<String>, bannerContent: List<String>, bannerBeanList: List<BannerBean>) {
        if (null != mBanner) {
            mBanner!!.setTag(bannerBeanList)
            LogUtils.d(TAG, "initBanner banner == $bannerList")
            mBanner!!.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
            mBanner!!.setImageLoader(GlideImageLoader())
            mBanner!!.setImages(bannerList)
            mBanner!!.setBannerTitles(bannerContent)
            mBanner!!.setIndicatorGravity(BannerConfig.RIGHT)
            mBanner!!.setDelayTime(2500)
            mBanner!!.setOnBannerListener { position: Int ->
                LogUtils.d(TAG, "OnBannerClick position == $position")
                //Banner 埋点
                val list = mBanner!!.getTag()
                if (list != null) {
                    list as List<BannerBean>
                    val bannerBean = list[position]
                    MobclickAgent.onEvent(TestApp.getContext(), "click_big_banner", bannerBean.targetName)
                    getBannerType(bannerBean)
                }
            }
            mBanner!!.start()
        } else {
            LogUtils.d(TAG, "initBanner  banner = null")
        }
    }

    override fun onResume() {
        super.onResume()
        /*广告栏开始自动轮播*/
        mBanner?.startAutoPlay()
    }

    override fun onPause() {
        super.onPause()
        /*广告栏停止自动轮播*/
        mBanner?.stopAutoPlay()
    }

    /**
     * 获取当前Banner和通知的类型 根据类型的不同执行不同的方法
     */
    protected fun getBannerType(jumpBean: IPageJumpBean) {
        when (jumpBean.type) {
            AppConstant.BANNER_TYPE_VIDEO -> {
                LogUtils.d(TAG, "BANNER_TYPE_VIDEO")
                ARouter.getInstance().build("/player/activity").withString("vodId", jumpBean.vodId).navigation()
            }
            AppConstant.BANNER_TYPE_ROUTER -> {
                LogUtils.d(TAG, "BANNER_TYPE_ROUTER")
                val path = jumpBean.androidRouter
                if (!TextUtils.isEmpty(path)) {
                    ARouter.getInstance().build(path).navigation()
                }
            }
            AppConstant.BANNER_TYPE_WEBURL -> {
                LogUtils.d(TAG, "BANNER_TYPE_WEBURL = " + jumpBean.webUrl)
                startActivity(IntentUtils.getBrowserIntent(jumpBean.webUrl))
                MobclickAgent.onEvent(TestApp.getContext(), "click_ads_banner", jumpBean.targetName)
            }
            AppConstant.BANNER_TYPE_TOPIC -> {
                LogUtils.d(TAG, "BANNER_TYPE_Topic")
                val pid = jumpBean.topicRouter.zt_pid
                val tag = jumpBean.topicRouter.zt_tag
                val type = jumpBean.topicRouter.zt_type
                ARouter.getInstance().build("/topicVideoList/activity")
                        .withInt("pid", pid)
                        .withString("tag", tag)
                        .withString("type", type)
                        .navigation()
            }
            AppConstant.BANNER_TYPE_AD -> {
                if (jumpBean is BannerBean) {
                    val adId = jumpBean.id
                    mPresenter.addAdInfo(AppConstant.AD_TYPE_BANNER, adId)
                }
                LogUtils.d(TAG, "BANNER_TYPE_WEBURL = " + jumpBean.webUrl)
                startActivity(IntentUtils.getBrowserIntent(jumpBean.webUrl))
                MobclickAgent.onEvent(TestApp.getContext(), "click_ads_banner", jumpBean.targetName)
            }
            else -> {
            }
        }
    }
}