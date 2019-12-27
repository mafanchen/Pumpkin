package com.video.test.module.feedback

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import butterknife.BindView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.video.test.R
import com.video.test.javabean.FeedbackBean
import com.video.test.ui.base.BaseActivity
import com.video.test.ui.widget.LoadingView
import com.video.test.ui.widget.RefreshHeader
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

@Route(path = "/feedback/history/activity")
class FeedbackHistoryActivity : BaseActivity<FeedbackHistoryPresenter>(), FeedbackHistoryContract.View {

    @JvmField
    @BindView(R.id.refresh_layout)
    var mSwipeRefresh: SmartRefreshLayout? = null

    @JvmField
    @BindView(R.id.rv_feedback)
    var mRv: RecyclerView? = null

    @JvmField
    @BindView(R.id.loadingView)
    var mLoadingView: LoadingView? = null

    private val mAdapter = MultiTypeAdapter()

    override fun getContextViewId(): Int = R.layout.bean_activity_feedback_history

    override fun initData() {
        mLoadingView?.setOnLoadingListener(object : LoadingView.OnLoadingListener {
            override fun onRetry() {
                mLoadingView?.showContent()
                mPresenter.getFeedbacks()
            }

            override fun onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation()
            }
        })

        initSwipeRefresh()
        mSwipeRefresh?.autoRefresh()
    }

    override fun initToolBar() {
        super.initToolBar()
        val ibBack = findViewById<ImageButton>(R.id.ib_back_toolbar)
        ibBack.visibility = View.VISIBLE
        ibBack.setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_title_toolbar).text = "反馈历史"
    }

    override fun setAdapter() {
        super.setAdapter()
        mAdapter.register(FeedbackBean::class.java, FeedbackHistoryViewBinder())
        mRv?.layoutManager = LinearLayoutManager(this)
        mRv?.adapter = mAdapter
    }

    override fun showRefreshLayout() {
        mSwipeRefresh?.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mSwipeRefresh?.finishRefresh(isSuccess)
    }

    override fun setData(items: Items) {
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }


    private fun initSwipeRefresh() {
        mSwipeRefresh?.setEnableLoadMore(false)
        mSwipeRefresh?.setRefreshHeader(RefreshHeader(this))
        mSwipeRefresh?.setOnRefreshListener { mPresenter.getFeedbacks() }
    }

    override fun showNetworkErrorView() {
        mLoadingView?.showError()
    }
}