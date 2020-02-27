package com.video.test.module.download

import android.app.Activity
import android.graphics.Color
import android.support.constraint.Group
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.AppConstant
import com.video.test.R
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.sp.SpUtils
import com.video.test.ui.base.BaseActivity
import com.video.test.utils.LogUtils
import com.video.test.utils.NetworkUtils
import com.video.test.utils.PixelUtils
import com.video.test.utils.ToastUtils
import com.yanzhenjie.recyclerview.swipe.SwipeMenu
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import java.util.*

@Route(path = "/download/videoList/activity")
class DownloadVideoListActivity : BaseActivity<DownloadVideoListPresenter>(), DownloadVideoListContract.View {

    private val TAG = "DownloadVideoListActivity"

    @JvmField
    @BindView(R.id.rv_download)
    var mRvDownload: SwipeMenuRecyclerView? = null
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
    @Autowired(name = "videoId")
    var mVideoId: String? = null
    override var adapter: DownloadAdapter? = null
    private var mM3U8List: List<M3U8DownloadBean>? = null
    private var mIsManager = false
    private var mSelectedSet: HashSet<String>? = HashSet()

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_download_video_list
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        NetworkUtils.checkNetConnectChange()
        requireStoragePerm()
        adapter = DownloadAdapter()
        /* 进入之前 先将所有的任务都变为暂停状态 防止因意外任务中断  有2个以上的 任务状态为Start 的任务  */
        mPresenter.videoId = mVideoId
        mPresenter.pauseAllTasks()
        mPresenter.getM3U8Tasks()
    }


    override fun initToolBar() {
        if (null != mTvTitle && null != mIbBack) {
            mIbBack!!.visibility = View.VISIBLE
            mTvTitle!!.setText(R.string.activity_download)
        }
        if (mTvEditBtn != null) {
            setManager(false)
        }
    }

    override fun setAdapter() {
        mRvDownload!!.setSwipeMenuCreator { _: SwipeMenu?, rightMenu: SwipeMenu, _: Int ->
            if (!mIsManager) {
                val width = PixelUtils.dp2px(this, 75f)
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val swipeMenuItem = SwipeMenuItem(this)
                        .setBackground(R.color.download_btn_delete)
                        .setText(R.string.activity_download_delete)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        .setTextSize(15)
                        .setHeight(height)
                        .setWidth(width)
                rightMenu.addMenuItem(swipeMenuItem)
            }
        }
        mRvDownload!!.setSwipeMenuItemClickListener { menuBridge: SwipeMenuBridge, position: Int ->
            LogUtils.d(TAG, "Menu position : " + position + " VideoName : " + mM3U8List!![position].videoName)
            menuBridge.closeMenu()
            mPresenter.deleteM3U8Task(mM3U8List!![position].videoUrl)
            setResult(Activity.RESULT_OK)
        }
        mRvDownload!!.adapter = adapter
        setListener()
    }

    /**
     * 设置监听器
     */
    private fun setListener() {
        adapter!!.setClickListener(object : DownloadItemClickListener {
            override fun onItemSelected(isSelected: Boolean, bean: M3U8DownloadBean) {
                if (mSelectedSet == null) {
                    mSelectedSet = HashSet()
                }
                if (isSelected) {
                    mSelectedSet!!.add(bean.videoUrl)
                } else {
                    mSelectedSet!!.remove(bean.videoUrl)
                }
                setSelectCountText(mSelectedSet!!.size)
                setSelectAllText(mSelectedSet!!.size)
            }

            override fun startTask(downloadingBean: M3U8DownloadBean, position: Int): Boolean {
                LogUtils.d(TAG, "startTask url : " + downloadingBean.videoUrl + " position : " + position)
                //这里开始任务时需要判断当前网络是否是移动网络和是否开启了移动网络下载
                if (NetworkUtils.isWifiConnected(this@DownloadVideoListActivity)) {
                    mPresenter.startM3U8Task(downloadingBean)
                } else {
                    val isMobileOpen = SpUtils.getBoolean(this@DownloadVideoListActivity, AppConstant.SWITCH_MOBILE_DOWN, true)
                    if (isMobileOpen) {
                        mPresenter.startM3U8Task(downloadingBean)
                    } else {
                        showMobileDownloadConfirmDialog()
                        return false
                    }
                }
                return true
            }

            override fun pauseTask(downloadingBean: M3U8DownloadBean, position: Int) {
                LogUtils.d(TAG, "pauseTask url : " + downloadingBean.videoUrl + " position : " + position)
                mPresenter.pauseM3U8Task(downloadingBean)
            }

            override fun downloadingTask(downloadingBean: M3U8DownloadBean, position: Int) {
                LogUtils.d(TAG, "downloadingTask url : " + downloadingBean.videoUrl + " position : " + position)
            }

            override fun playNetworkVideo(videoId: String, videoUrl: String) {
                MaterialDialog.Builder(this@DownloadVideoListActivity)
                        .content(R.string.dialog_playVideo)
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            ARouter.getInstance()
                                    .build("/player/activity")
                                    .withString("vodId", videoId)
                                    .withString("videoUrl", videoUrl)
                                    .withString("videoDegree", "0")
                                    .navigation()
                        }.show()
            }

            override fun playLocalVideo(videoUrl: String, localUrl: String, localName: String) {
                MaterialDialog.Builder(this@DownloadVideoListActivity)
                        .content(R.string.dialog_playVideo)
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            ARouter.getInstance().build("/localPlayer/activity")
                                    .withString("localUrl", localUrl)
                                    .withString("localName", localName)
                                    .withString("videoUrl", videoUrl)
                                    .navigation()
                        }.show()
            }
        })
    }

    private fun showMobileDownloadConfirmDialog() {
        MaterialDialog.Builder(this)
                .content("移动数据网络下以为您暂停缓存，待接入WIFI时自动开始缓存\n如您需要在移动数据网络下缓存，请到“设置”中开启")
                .negativeColor(Color.parseColor("#888888"))
                .onNegative { dialog, _ -> dialog.dismiss() }
                .negativeText("只在WIFI缓存")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                    dialog.dismiss()
                    ARouter.getInstance().build("/setting/activity").navigation()
                }
                .positiveText("去设置")
                .build()
                .show()
    }

    override fun showNoCacheBackground() {
        if (null != mIvNoCache && null != mRvDownload) {
            mIvNoCache!!.visibility = View.VISIBLE
            mRvDownload!!.visibility = View.GONE
        }
    }

    override fun hideNoCacheBackground() {
        if (null != mIvNoCache && null != mRvDownload) {
            mIvNoCache!!.visibility = View.GONE
            mRvDownload!!.visibility = View.VISIBLE
        }
    }

    override fun setDownloadBeans(downloadBeans: List<M3U8DownloadBean>) {
        if (null != adapter && mRvDownload != null) {
            mM3U8List = downloadBeans
            adapter!!.data = downloadBeans
        }
    }

    private fun setManager(isManager: Boolean) {
        mIsManager = isManager
        if (!isManager) {
            mTvEditBtn!!.setText(R.string.activity_download_edit)
            mTvEditBtn!!.setTextColor(Color.parseColor("#ffad43"))
            mGroup!!.visibility = View.GONE
            if (adapter != null) {
                adapter!!.setIsManager(mIsManager)
            }
        } else {
            mTvEditBtn!!.setText(R.string.activity_download_edit_cancel)
            mTvEditBtn!!.setTextColor(Color.parseColor("#cccccc"))
            //            mPresenter.getM3U8Tasks(mUserLevel);
            mGroup!!.visibility = View.VISIBLE
            mIsManager = true
            if (adapter != null) {
                adapter!!.setIsManager(mIsManager)
            }
        }
        deselectAll()
    }

    @OnClick(R.id.ib_back_toolbar, R.id.tv_editBtn_toolbar, R.id.tv_selectAll, R.id.tv_delete)
    fun onClick(v: View) {
        when (v.id) {
            R.id.ib_back_toolbar -> finish()
            R.id.tv_editBtn_toolbar -> if (null != mTvEditBtn && null != mGroup) {
                if (View.VISIBLE == mGroup!!.visibility) {
                    setManager(false)
                } else {
                    setManager(true)
                }
            }
            R.id.tv_selectAll -> {
                if (adapter == null || mM3U8List == null || mM3U8List!!.isEmpty()) {
                    return
                }
                if (mSelectedSet == null || mSelectedSet!!.isEmpty()) {
                    selectAll()
                } else {
                    val allTaskCount = mM3U8List!!.size
                    val selectCount = mSelectedSet!!.size
                    if (allTaskCount > selectCount) {
                        selectAll()
                    } else {
                        deselectAll()
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
            R.id.tv_delete -> if (mSelectedSet != null) {
                if (mSelectedSet!!.size >= 2) { //大于两个弹窗
                    showDeleteConfirmDialog()
                } else if (mSelectedSet!!.size > 0) {
                    deleteSelected()
                }
            }
        }
    }

    private fun deleteSelected() { //批量删除
        mPresenter.deleteM3U8Task(*mSelectedSet!!.toArray(arrayOf()))
        setManager(false)
        setResult(Activity.RESULT_OK)
    }

    private fun showDeleteConfirmDialog() {
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
                    deleteSelected()
                    dialog.dismiss()
                }
                .positiveText("确定")
                .build()
                .show()
    }

    private fun selectAll() {
        if (adapter != null) {
            adapter!!.selectAll()
            adapter!!.notifyDataSetChanged()
        }
        if (mM3U8List != null) {
            for (o in mM3U8List!!) {
                if (mSelectedSet != null) {
                    mSelectedSet!!.add(o.videoUrl)
                }
            }
        }
        if (mSelectedSet != null) {
            setSelectCountText(mSelectedSet!!.size)
            setSelectAllText(mSelectedSet!!.size)
        }
    }

    private fun deselectAll() {
        if (adapter != null) {
            adapter!!.deSelectAll()
            adapter!!.notifyDataSetChanged()
        }
        if (mSelectedSet != null) {
            mSelectedSet!!.clear()
        }
        setSelectCountText(0)
        setSelectAllText(0)
    }

    private fun setSelectAllText(count: Int) {
        if (mTvSelectAll == null) {
            return
        }
        if (count == 0 || mM3U8List == null || mM3U8List!!.isEmpty()) {
            mTvSelectAll!!.setText(R.string.activity_download_selectAll)
        } else {
            val allTaskCount = mM3U8List!!.size
            if (allTaskCount > count) {
                mTvSelectAll!!.setText(R.string.activity_download_selectAll)
            } else {
                mTvSelectAll!!.setText(R.string.activity_download_deselectAll)
            }
        }
    }

    private fun setSelectCountText(count: Int) {
        if (mTvDelete != null) {
            mTvDelete!!.isEnabled = count > 0
            if (count <= 0) {
                mTvDelete!!.setText(R.string.activity_download_delete)
            } else {
                val text = getString(R.string.activity_download_delete).toString() + "(" + count + ")"
                mTvDelete!!.text = text
            }
        }
    }

    override fun showEditBtn() {
        if (mTvEditBtn != null) {
            mTvEditBtn!!.visibility = View.VISIBLE
        }
    }

    override fun hideEditBtn() {
        if (mTvEditBtn != null) {
            mTvEditBtn!!.visibility = View.GONE
        }
    }

    override fun showToast(text: String) {
        runOnUiThread { ToastUtils.showToast(text) }
    }

}