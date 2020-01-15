package com.video.test.module.videotype

import android.annotation.SuppressLint
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.*
import com.video.test.module.videorecommend.VideoRecommendHorizontalViewBinder
import com.video.test.module.videorecommend.VideoRecommendViewBinder
import com.video.test.ui.adapter.ColumnVideoHorizontalAdapter
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.viewbinder.VideoAdItemViewBinder
import com.video.test.ui.viewbinder.VideoTitleItemViewBinder
import com.video.test.ui.widget.GlideOnScrollListener
import me.drakeet.multitype.MultiTypeAdapter

class SpecialFragment : BaseVideoTypeListFragment<SpecialPresenter>(), SpecialVideoListContract.View {

    companion object {
        fun newInstance(): SpecialFragment {
            val fragment = SpecialFragment()
            fragment.pid = 7
            return fragment
        }
    }

    @JvmField
    var mIvTopVideo: ImageView? = null
    @JvmField
    var mTvTopVideo: TextView? = null
    @JvmField
    var mIvColumnTitle: ImageView? = null
    @JvmField
    var mTvColumnTitle: TextView? = null
    @JvmField
    var mRvVideo: RecyclerView? = null

    private var mAdapterColumn: ColumnVideoHorizontalAdapter? = null

    override fun setAdapter() {
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
        mRvVideoList?.addOnScrollListener(GlideOnScrollListener())
        initAdapter()
        mRvVideoList?.adapter = mAdapter


        mAdapterColumn = ColumnVideoHorizontalAdapter()
        mRvVideo?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRvVideo?.adapter = mAdapterColumn
        //TODO 这里可以对横向的recyclerView滑动手势做处理，使其在横划时，不会触发到viewPager的滑动
    }

    override fun initAdapter() {
        if (mAdapter == null) {
            mAdapter = MultiTypeAdapter()
            mAdapter!!.register(VideoTitleBean::class.java, VideoTitleItemViewBinder(true))
            val videoAdItemViewBinder = VideoAdItemViewBinder()
            videoAdItemViewBinder.setOnAdClickListener { mPresenter.addAdInfo(adType, it) }
            mAdapter!!.register(AdInfoBean::class.java, videoAdItemViewBinder)
            mAdapter!!.register(VideoRecommendBean::class.java, VideoRecommendHorizontalViewBinder(true))
            mAdapter!!.register(VideoBean::class.java, VideoRecommendViewBinder(true))
            mAdapter!!.register(FooterViewBean::class.java, FooterViewBinder())
        }
    }

    override fun initViewBeforeLoadData() {
        mIvTopVideo = view?.findViewById(R.id.iv_top_video)
        mTvTopVideo = view?.findViewById(R.id.tv_top_video)
        mTvColumnTitle = view?.findViewById(R.id.tv_title_video_column)
        mIvColumnTitle = view?.findViewById(R.id.iv_title_video_column)
        mRvVideo = view?.findViewById(R.id.rv_video_horizontal)
        mSwipeRefresh = view?.findViewById(R.id.refresh_videoList_Fragment)
        mRvVideoList = view?.findViewById(R.id.rv_videoList_Fragment)
        mLoadingView = view?.findViewById(R.id.loadingView)
    }

    override fun getContentViewId(): Int = R.layout.bean_fragment_video_list_special

    override fun onRefresh() {
        //无需获取banner
        mPresenter.getHomePageVideoList(pid)
    }

    @SuppressLint("SetTextI18n")
    override fun setTopVideo(topVideo: SpecialVideoTopVideoBean?) {
        if (topVideo != null) {
            mIvTopVideo?.let {
                GlideApp.with(this)
                        .load(topVideo.imageUrl)
                        .override(it.width, it.height)
                        .centerCrop()
                        .into(it)
            }
            mTvTopVideo?.text = "${topVideo.name}-${topVideo.content}"
            mIvTopVideo?.let { it.setTag(it.id, topVideo) }
            mIvTopVideo?.setOnClickListener {
                val tag = it.getTag(it.id)
                if (tag is SpecialVideoTopVideoBean) {
                    ARouter.getInstance().build("/player/activity").withString("vodId", tag.vodId).navigation()
                }
            }
        }
    }

    override fun setVideoColumn(columnBean: SpecialVideoColumnBean?) {
        if (columnBean != null) {
            if (!TextUtils.isEmpty(columnBean.image) && mIvColumnTitle != null) {
                GlideApp.with(this).load(columnBean.image).into(mIvColumnTitle!!)
            }
            mTvColumnTitle?.text = columnBean.name
            mAdapterColumn?.setData(columnBean.list)
            mAdapterColumn?.notifyDataSetChanged()
        }
    }

}