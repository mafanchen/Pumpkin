package com.video.test.module.beantopic

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.video.test.R
import com.video.test.javabean.BeanTopicBean
import com.video.test.javabean.FooterViewBean
import com.video.test.ui.base.BaseActivity
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.widget.GlideOnScrollListener
import com.video.test.ui.widget.LoadingView
import com.video.test.ui.widget.RefreshHeader
import com.video.test.utils.LogUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

@Route(path = "/topic/activity")
class TopicActivity : BaseActivity<BeanTopicPresenter>(), BeanTopicContract.View {

    companion object {
        const val TAG = "TopicActivity"
    }

    @BindView(R.id.tv_title_toolbar)
    lateinit var mTvTitle: TextView
    @BindView(R.id.ib_back_toolbar)
    lateinit var mIbBack: ImageButton
    @BindView(R.id.rv_beanTopic_fragment)
    lateinit var mRecycleView: RecyclerView
    @BindView(R.id.layout_refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout
    @BindView(R.id.loadingView)
    lateinit var mLoadingView: LoadingView
    private var mAdapter: MultiTypeAdapter? = null
    private var mItems: Items? = null

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_topic
    }

    override fun initData() {
        mLoadingView.setOnLoadingListener(object : LoadingView.OnLoadingListener {
            override fun onRetry() {
                mLoadingView.showContent()
                mPresenter.getHomepageBeanTopicList()
            }

            override fun onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation()
            }
        })
        mRefreshLayout.setEnableLoadMore(false)
        mRefreshLayout.setRefreshHeader(RefreshHeader(this))
        mRefreshLayout.setOnRefreshListener { mPresenter.getHomepageBeanTopicList() }
        mRefreshLayout.autoRefresh()
    }

    override fun setAdapter() {
        super.setAdapter()
        mAdapter = MultiTypeAdapter()
        mAdapter!!.register(BeanTopicBean::class.java, BeanTopicViewBinder())
        mAdapter!!.register(FooterViewBean::class.java, FooterViewBinder())
        mRecycleView.isNestedScrollingEnabled = false
        mRecycleView.addOnScrollListener(GlideOnScrollListener())
        mRecycleView.adapter = mAdapter
    }

    override fun setHomepageBeanTopicList(beanTopicList: MutableList<BeanTopicBean>) {
        if (null == mItems) {
            LogUtils.d(TAG, "setHomepageBeanTopicList mItem not exist")
            mItems = Items()
            mItems!!.addAll(beanTopicList)
            mItems!!.add(FooterViewBean())
            mAdapter?.items = mItems!!
            mAdapter?.notifyDataSetChanged()
        } else {
            LogUtils.d(TAG, "setHomepageBeanTopicList mItem not exist")
            mItems!!.clear()
            mItems!!.addAll(beanTopicList)
            mItems!!.add(FooterViewBean())
            mAdapter?.items = mItems!!
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun showRefreshLayout() {
        mRefreshLayout.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mRefreshLayout.finishRefresh(isSuccess)
    }


    @OnClick(R.id.ib_back_toolbar)
    fun onViewClick(view: View) {
        when (view.id) {
            R.id.ib_back_toolbar -> finish()
        }
    }

    override fun initToolBar() {
        mTvTitle.text = "专题推荐"
        mIbBack.visibility = View.VISIBLE
    }

    override fun showNetworkErrorView() {
        mLoadingView.showError()
    }
}