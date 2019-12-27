package com.video.test.module.swap

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.video.test.R
import com.video.test.javabean.GiftBean
import com.video.test.javabean.ShareExchangeListBean
import com.video.test.ui.base.BaseFragment
import com.video.test.ui.widget.RegisterDialogFragment
import me.drakeet.multitype.MultiTypeAdapter

class SwapListFragment : BaseFragment<SwapListPresenter>(), SwapListContract.View {

    @JvmField
    @BindView(R.id.swipeRefresh_swap)
    var mSwipeRefresh: SwipeRefreshLayout? = null
    @JvmField
    @BindView(R.id.rv_swap_type)
    var mRv: RecyclerView? = null

    private var mLoadingDialog: MaterialDialog? = null

    private var mAdapter: MultiTypeAdapter? = null
    private var mItemBinder: SwapTypeViewBinder? = null


    override fun getContentViewId(): Int {
        return R.layout.bean_fragment_swap_list
    }

    override fun initView() {
        super.initView()
        initSwipeRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.getShareExchange()
    }

    override fun loadData() {}

    private fun initSwipeRefresh() {
        mSwipeRefresh?.setColorSchemeColors(Color.parseColor("#ff9900"), Color.parseColor("#aaaaaa"))
        mSwipeRefresh?.setOnRefreshListener { mPresenter.getShareExchange() }
    }

    override fun showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = MaterialDialog.Builder(mContext)
                    .progress(true, 1)
                    .content("请稍候...")
                    .canceledOnTouchOutside(false)
                    .build()
        }
        mLoadingDialog?.show()
    }

    override fun hideLoading() {
        mLoadingDialog?.dismiss()
    }

    override fun setAdapter() {
        super.setAdapter()
        if (mRv?.layoutManager == null) {
            mRv?.layoutManager = LinearLayoutManager(mContext)
        }
        if (mRv?.adapter == null) {
            mAdapter = MultiTypeAdapter()
            mItemBinder = SwapTypeViewBinder()
            mAdapter?.register(ShareExchangeListBean.ShareExchangeBean::class.java, mItemBinder!!)
            mItemBinder?.setOnSwapClickListener { bean -> showConfirmDialog(bean) }
            mRv?.adapter = mAdapter
        }
    }

    private fun showConfirmDialog(bean: ShareExchangeListBean.ShareExchangeBean) {
        MaterialDialog.Builder(mContext)
                .title("提示")
                .content(getString(R.string.activity_swap_message_confirm, bean.shareSetTime, bean.shareName, bean.shareSetNum))
                .positiveColorRes(R.color.swap_text_friend_count)
                .positiveText(R.string.activity_swap_text_confirm)
                .onPositive { _, _ -> mPresenter.getShareVip(bean.id, bean.shareSetNum) }
                .negativeText(R.string.activity_swap_text_cancel)
                .build()
                .show()
    }


    override fun setRefresh(refresh: Boolean) {
        mSwipeRefresh?.isRefreshing = refresh
    }

    override fun setShareNum(shareNum: Int) {
        mItemBinder?.setShareNum(shareNum)
        mAdapter?.notifyDataSetChanged()
    }

    override fun setShareExchange(shareExchange: List<ShareExchangeListBean.ShareExchangeBean>) {
        mAdapter?.items = shareExchange
        mAdapter?.notifyDataSetChanged()
    }

    override fun showSwapSuccessDialog(bean: GiftBean) {
        if (!TextUtils.isEmpty(bean.activity_pic)) {
            val bundle = Bundle()
            bundle.putString("closeUrl", bean.close_pic)
            bundle.putString("picUrl", bean.activity_pic)
            bundle.putString("expireTime", bean.vip_time)

            val registerDialogFragment = RegisterDialogFragment.newInstance(bundle)
            activity?.let { registerDialogFragment.show(activity!!.supportFragmentManager, "giftDialog") }
            registerDialogFragment.setDialogItemClickListener { registerDialogFragment.dismiss() }
        }
    }
}