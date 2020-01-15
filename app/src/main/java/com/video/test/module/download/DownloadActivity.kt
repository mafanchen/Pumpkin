package com.video.test.module.download

import android.graphics.Color
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.video.test.R
import com.video.test.javabean.DownloadBean
import com.video.test.ui.adapter.DownloadVideoAdapter
import com.video.test.ui.base.BaseActivity
import com.video.test.utils.ToastUtils

@Route(path = "/download/activity")
class DownloadActivity : BaseActivity<DownloadPresenter>(), DownloadContract.View {
    private val TAG = "DownloadActivity"

    @JvmField
    @BindView(R.id.rv_download)
    var mRvDownload: RecyclerView? = null
    @JvmField
    @BindView(R.id.iv_background_noCache_download)
    var mIvNoCache: TextView? = null
    @JvmField
    @BindView(R.id.tv_title_toolbar)
    var mTvTitle: TextView? = null
    @JvmField
    @BindView(R.id.ib_back_toolbar)
    var mIbBack: ImageButton? = null
    @JvmField
    @BindView(R.id.tv_editBtn_toolbar)
    var mTvEditBtn: TextView? = null
    @JvmField
    @BindView(R.id.group_delete)
    var mGroup: Group? = null
    @JvmField
    @BindView(R.id.tv_selectAll)
    var mTvSelectAll: TextView? = null
    @JvmField
    @BindView(R.id.tv_delete)
    var mTvDelete: TextView? = null
    @JvmField
    @BindView(R.id.progress_sd_card)
    var mProgressSDCar: ProgressBar? = null
    @JvmField
    @BindView(R.id.tv_sd_card)
    var mTvSDCard: TextView? = null

    override var adapter: DownloadVideoAdapter? = null

    override fun getContextViewId(): Int = R.layout.bean_activity_download

    override fun initToolBar() {
        super.initToolBar()
        mIbBack?.visibility = View.VISIBLE
        mTvTitle?.setText(R.string.activity_download)
        setEditMode(false)
        mTvEditBtn?.visibility = View.GONE
    }

    override fun initData() {
        mPresenter.getAllVideoTasks()
        mPresenter.getSDCardFreeSize()
    }

    override fun setAdapter() {
        adapter = DownloadVideoAdapter()
        adapter!!.onSelectListener = object : DownloadVideoAdapter.OnSelectListener {
            override fun onSelect(isSelected: Boolean, bean: DownloadBean) {
                mPresenter.onSelectVideo(isSelected, bean)
            }
        }
        mRvDownload?.layoutManager = LinearLayoutManager(this)
        mRvDownload?.adapter = adapter
    }

    @OnClick(R.id.ib_back_toolbar, R.id.tv_editBtn_toolbar, R.id.tv_selectAll, R.id.tv_delete)
    fun onViewClick(view: View) {
        when (view.id) {
            //点击编辑按钮
            R.id.tv_editBtn_toolbar -> {
                val isEditMode = mGroup!!.visibility == View.VISIBLE
                setEditMode(!isEditMode)
                mPresenter.deselectAll()
            }
            R.id.tv_selectAll -> mPresenter.onSelectAllClick()
            R.id.tv_delete -> mPresenter.onDeleteClick()
            R.id.ib_back_toolbar -> finish()
        }
    }

    override fun setSDCardProgress(used: String, total: String, percent: Int) {
        mTvSDCard?.text = getString(R.string.activity_download_sdCard, used, total)
        mProgressSDCar?.progress = percent
    }

    override fun showNoCacheBackground() {
        mIvNoCache?.visibility = View.VISIBLE
    }

    override fun hideNoCacheBackground() {
        mIvNoCache?.visibility = View.GONE
    }

    override fun showEditBtn() {
        mTvEditBtn?.visibility = View.VISIBLE
    }

    override fun hideEditBtn() {
        mTvEditBtn?.visibility = View.GONE
    }

    override fun setEditMode(isEditMode: Boolean) {
        if (isEditMode) {
            mTvEditBtn?.setText(R.string.activity_download_edit_cancel)
            mTvEditBtn?.setTextColor(Color.parseColor("#cccccc"))
            mGroup?.visibility = View.VISIBLE
        } else {
            mGroup?.visibility = View.GONE
            mTvEditBtn?.setText(R.string.activity_download_edit)
            mTvEditBtn?.setTextColor(Color.parseColor("#ffad43"))
        }
        adapter?.isManager = isEditMode
    }

    override fun setSelectCountText(text: String) {
        mTvDelete?.text = text
    }

    override fun setSelectAllText(text: String) {
        mTvSelectAll?.text = text
    }

    override fun setDeleteBtnEnable(enable: Boolean) {
        mTvDelete?.isEnabled = enable
    }

    override fun showDeleteConfirmDialog() {
        MaterialDialog.Builder(this)
                .titleColor(Color.parseColor("#333333"))
                .title("确定删除已经缓存视频？")
                .contentColor(Color.parseColor("#888888"))
                .content("删除后无法恢复")
                .negativeColor(Color.parseColor("#888888"))
                .onNegative { dialog: MaterialDialog, _: DialogAction? -> dialog.dismiss() }
                .negativeText("取消")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                    mPresenter.deleteSelected()
                    dialog.dismiss()
                }
                .positiveText("确定")
                .build()
                .show()
    }

    override fun showToast(it: String) {
        ToastUtils.showToast(it)
    }

    override fun setResultOk() {
        setResult(RESULT_OK)
    }
}