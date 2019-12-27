package com.video.test.module.simplevideo

import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.video.test.R
import com.video.test.javabean.*
import com.video.test.module.videorecommend.VideoRecommendHorizontalViewBinder
import com.video.test.module.videorecommend.VideoRecommendViewBinder
import com.video.test.ui.base.BaseFragment
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.viewbinder.RecommendVideoTitleItemViewBinder
import com.video.test.ui.viewbinder.VideoAdItemViewBinder
import com.video.test.ui.widget.DividerItemDecoration
import com.video.test.ui.widget.GlideOnScrollListener
import com.video.test.ui.widget.RefreshHeader
import com.video.test.utils.PixelUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

class SimpleVideoFragment : BaseFragment<SimpleVideoPresenter>(), SimpleVideoContract.View {

    companion object {
        private const val SPAN_COUNT = 6
    }

    @JvmField
    @BindView(R.id.layout_refresh)
    var mLayoutRefresh: SmartRefreshLayout? = null

    @JvmField
    @BindView(R.id.rv_video_list)
    var mRvVideoList: RecyclerView? = null

    private var mAdapter: MultiTypeAdapter? = null

    override fun showRefreshLayout() {
        mLayoutRefresh?.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mLayoutRefresh?.finishRefresh(isSuccess)
    }

    override fun setVideoData(items: Items) {
        mAdapter?.items = items
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadData() {
        initSwipeRefresh()
        mLayoutRefresh?.autoRefresh()
    }

    override fun getContentViewId(): Int {
        return R.layout.bean_fragment_simple_video
    }

    override fun setAdapter() {
        super.setAdapter()
        val leftRight = PixelUtils.dp2px(mContext, 3f)
        mAdapter = MultiTypeAdapter()
        mAdapter!!.register(VideoTitleBean::class.java, RecommendVideoTitleItemViewBinder())
        mAdapter!!.register(AdInfoBean::class.java, VideoAdItemViewBinder())
        mAdapter!!.register(VideoRecommendBean::class.java, VideoRecommendHorizontalViewBinder())
        mAdapter!!.register(VideoBean::class.java, VideoRecommendViewBinder())
        mAdapter!!.register(FooterViewBean::class.java, FooterViewBinder())
        val gridLayoutManager = GridLayoutManager(context, SPAN_COUNT)

        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                val list = mAdapter!!.items
                val item = list[position]
                return if (item is VideoTitleBean || item is FooterViewBean || item is AdInfoBean)
                    SPAN_COUNT
                else
                    if (item is VideoRecommendBean) 3 else 2
            }
        }

        gridLayoutManager.spanSizeLookup = spanSizeLookup
        mRvVideoList?.layoutManager = gridLayoutManager
        mRvVideoList?.isNestedScrollingEnabled = false
        mRvVideoList?.setHasFixedSize(true)
        mRvVideoList?.adapter = mAdapter
        mRvVideoList?.addItemDecoration(DividerItemDecoration(leftRight, leftRight, Color.WHITE))
        mRvVideoList?.addOnScrollListener(GlideOnScrollListener())
    }

    fun initSwipeRefresh() {
        mLayoutRefresh?.setEnableLoadMore(false)
        mLayoutRefresh?.setRefreshHeader(RefreshHeader(mContext))
        mLayoutRefresh?.setOnRefreshListener { _ -> mPresenter.getHomePageVideoList() }
    }

}