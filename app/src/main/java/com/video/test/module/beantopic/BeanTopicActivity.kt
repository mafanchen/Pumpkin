package com.video.test.module.beantopic

import android.view.View
import butterknife.OnClick
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.R
import com.video.test.javabean.BeanTopicBean
import com.video.test.javabean.FooterViewBean
import com.video.test.ui.base.BaseActivity
import com.video.test.ui.viewbinder.FooterViewBinder
import com.video.test.ui.widget.GlideOnScrollListener
import com.video.test.ui.widget.LoadingView
import com.video.test.ui.widget.RefreshHeader
import com.video.test.utils.LogUtils
import kotlinx.android.synthetic.Test1.bean_fragment_bean_topic.*
import kotlinx.android.synthetic.Test1.bean_include_toolbar_new.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

@Route(path = "/topic/activity")
class BeanTopicActivity : BaseActivity<BeanTopicPresenter>(), BeanTopicContract.View {

    companion object {
        const val TAG = "TopicActivity"
    }

    private var mAdapter: MultiTypeAdapter? = null
    private var mItems: Items? = null

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_topic
    }

    override fun initData() {
        loadingView.setOnLoadingListener(object : LoadingView.OnLoadingListener {
            override fun onRetry() {
                loadingView.showContent()
                mPresenter.getHomepageBeanTopicList()
            }

            override fun onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation()
            }
        })
        layout_refresh.setEnableLoadMore(false)
        layout_refresh.setRefreshHeader(RefreshHeader(this))
        layout_refresh.setOnRefreshListener { mPresenter.getHomepageBeanTopicList() }
        layout_refresh.autoRefresh()
    }

    override fun setAdapter() {
        super.setAdapter()
        mAdapter = MultiTypeAdapter()
        mAdapter!!.register(BeanTopicBean::class.java, BeanTopicViewBinder())
        mAdapter!!.register(FooterViewBean::class.java, FooterViewBinder())
        rv_beanTopic_fragment.isNestedScrollingEnabled = false
        rv_beanTopic_fragment.addOnScrollListener(GlideOnScrollListener())
        rv_beanTopic_fragment.adapter = mAdapter
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
        layout_refresh.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        layout_refresh.finishRefresh(isSuccess)
    }


    @OnClick(R.id.ib_back_toolbar)
    fun onViewClick(view: View) {
        when (view.id) {
            R.id.ib_back_toolbar -> finish()
        }
    }

    override fun initToolBar() {
        tv_title_toolbar.text = "专题推荐"
        ib_back_toolbar.visibility = View.VISIBLE
    }

    override fun showNetworkErrorView() {
        loadingView.showError()
    }
}