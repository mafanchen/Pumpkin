package com.video.test.module.videotype

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.video.test.javabean.*
import com.video.test.javabean.event.HotSearchWordRetryEvent
import com.video.test.module.videorecommend.VideoRecommendHorizontalViewBinder
import com.video.test.module.videorecommend.VideoRecommendViewBinder
import com.video.test.ui.base.BaseLazyFragment
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.viewbinder.VideoAdItemViewBinder
import com.video.test.ui.viewbinder.VideoTitleItemViewBinder
import com.video.test.ui.widget.DividerItemDecoration
import com.video.test.ui.widget.GlideOnScrollListener
import com.video.test.ui.widget.LoadingView
import com.video.test.ui.widget.RefreshHeader
import com.video.test.utils.PixelUtils
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

}