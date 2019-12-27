package com.video.test.module.swap

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.BindView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.video.test.R
import com.video.test.javabean.SwapHistoryBean
import com.video.test.ui.base.BaseFragment
import com.video.test.ui.widget.RefreshHeader
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

class SwapHistoryFragment : BaseFragment<SwapHistoryPresenter>(), SwapHistoryContract.View {

    @JvmField
    @BindView(R.id.rv_swap_history)
    var mRv: RecyclerView? = null
    @JvmField
    @BindView(R.id.swipeRefresh_swap)
    var mRefreshLayout: SmartRefreshLayout? = null
    private var mAdapter: MultiTypeAdapter? = null

    private var mItemBinder: SwapHistoryViewBinder? = null

    override fun getContentViewId(): Int {
        return R.layout.bean_fragment_swap_history_list
    }

    override fun initView() {
        super.initView()
        initSwipeRefresh()
    }

    override fun loadData() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mPresenter.refresh()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.refresh()
    }

    private fun initSwipeRefresh() {
        mRefreshLayout?.setRefreshHeader(RefreshHeader(mContext))
        mRefreshLayout?.setRefreshFooter(ClassicsFooter(mContext))
        mRefreshLayout?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mPresenter.getMore()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                mPresenter.refresh()
            }
        })
    }

    override fun setAdapter() {
        super.setAdapter()
        if (mRv?.layoutManager == null) {
            mRv?.layoutManager = LinearLayoutManager(mContext)
            val decoration = DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
            val drawable = ContextCompat.getDrawable(mContext, R.drawable.shape_bg_item_divider_1dp)!!
            decoration.setDrawable(drawable)
            mRv?.addItemDecoration(decoration)
        }
        if (mRv?.adapter == null) {
            mAdapter = MultiTypeAdapter()
            mItemBinder = SwapHistoryViewBinder()
            mAdapter?.register(SwapHistoryBean::class.java, mItemBinder!!)
            mRv?.adapter = mAdapter
        }
    }

    override fun showRefreshLayout() {
        mRefreshLayout?.autoRefreshAnimationOnly()
    }

    override fun hideRefreshLayout(isSuccess: Boolean) {
        mRefreshLayout?.finishRefresh(isSuccess)
    }

    override fun finishLoadMoreWithNoMoreData() {
        mRefreshLayout?.finishLoadMoreWithNoMoreData()
    }

    override fun loadMoreComplete() {
        mRefreshLayout?.finishLoadMore()
    }

    override fun setData(list: List<SwapHistoryBean>) {
        mAdapter?.items = list
        mAdapter?.notifyDataSetChanged()
    }

    override fun addData(list: List<SwapHistoryBean>) {
        val data = Items()
        if (mAdapter != null && mAdapter?.items != null) {
            data.addAll(mAdapter!!.items)
        }
        data.addAll(list)
        mAdapter?.items = data
        mAdapter?.notifyDataSetChanged()
    }

}