package com.video.test.module.hottest

import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.video.test.R
import com.video.test.javabean.VideoBean
import com.video.test.module.videorecommend.VideoRecommendViewBinder
import com.video.test.ui.base.BaseActivity
import com.video.test.ui.widget.DividerItemDecoration
import com.video.test.ui.widget.GlideOnScrollListener
import com.video.test.ui.widget.LoadingView
import com.video.test.ui.widget.RefreshHeader
import com.video.test.utils.PixelUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

@Route(path = "/hottest/activity")
class HottestVideoListActivity : BaseActivity<HottestVideoListPresenter>(), HottestVideoListContract.View {
    @BindView(R.id.tv_title_toolbar)
    lateinit var mTvTitle: TextView
    @BindView(R.id.ib_back_toolbar)
    lateinit var mIbBack: ImageButton
    @BindView(R.id.rv_recommend_video)
    lateinit var mRecycleView: RecyclerView
    @BindView(R.id.refresh_layout)
    lateinit var mSwipeRefresh: SmartRefreshLayout
    @BindView(R.id.loadingView)
    lateinit var mLoadingView: LoadingView
    @BindView(R.id.iv_search_toolbar)
    lateinit var mIvSearch: ImageView
    private var mAdapter: MultiTypeAdapter? = null

    @JvmField
    @Autowired(name = "showId")
    var mShowId: String? = null

    @JvmField
    @Autowired(name = "title")
    var mTitle: String? = null

    @JvmField
    @Autowired(name = "showPid")
    var mShowPid: String? = null

    @JvmField
    @Autowired(name = "vodPid")
    var mVodPid: Int = 0

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_recommend_video
    }

    override fun initToolBar() {
        mTvTitle.text = mTitle
        mIbBack.visibility = View.VISIBLE
        mIvSearch.visibility = View.VISIBLE
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        mLoadingView.setOnLoadingListener(object : LoadingView.OnLoadingListener {
            override fun onRetry() {
                mLoadingView.showContent()
                mShowId?.let { mPresenter.getHottestVideo(it, mShowPid) }
            }

            override fun onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation()
            }
        })
        initSwipeRefresh()
        mSwipeRefresh.autoRefresh()
    }

    override fun showRefreshLayout() {
        mSwipeRefresh.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mSwipeRefresh.finishRefresh(isSuccess)
    }

    private fun initSwipeRefresh() {
        mSwipeRefresh.setEnableLoadMore(false)
        mSwipeRefresh.setRefreshHeader(RefreshHeader(this))
        mSwipeRefresh.setOnRefreshListener {
            mShowId?.let { mPresenter.getHottestVideo(it, mShowPid) }
        }
    }

    override fun setAdapter() {
        super.setAdapter()
        mAdapter = MultiTypeAdapter()
        mAdapter!!.register(VideoBean::class.java, VideoRecommendViewBinder(mVodPid))
        val layoutManger = GridLayoutManager(this, 3)
        val leftRight = PixelUtils.dp2px(this, 3f)
        mRecycleView.addItemDecoration(DividerItemDecoration(leftRight, leftRight, Color.WHITE))
        mRecycleView.addOnScrollListener(GlideOnScrollListener())
        mRecycleView.layoutManager = layoutManger
        mRecycleView.adapter = mAdapter
    }


    override fun setVideoData(items: Items) {
        mAdapter?.items = items
        mAdapter?.notifyDataSetChanged()
    }

    @OnClick(R.id.ib_back_toolbar, R.id.iv_search_toolbar)
    fun onViewClick(view: View) {
        when (view.id) {
            R.id.ib_back_toolbar -> finish()
            R.id.iv_search_toolbar -> ARouter.getInstance().build("/search/activity").navigation()
        }
    }

    override fun showNetworkErrorView() {
        mLoadingView.showError()
    }
}