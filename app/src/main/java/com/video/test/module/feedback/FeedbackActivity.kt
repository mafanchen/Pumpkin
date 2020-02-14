package com.video.test.module.feedback

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnTextChanged
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.video.test.AppConstant
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.FeedbackTypeBean
import com.video.test.ui.base.BaseActivity
import com.video.test.utils.ToastUtils
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

@Route(path = "/feedback/activity")
class FeedbackActivity : BaseActivity<FeedbackPresenter>(), FeedbackContract.View {

    @JvmField
    @BindView(R.id.tv_feedback_content_count)
    var mTvCount: TextView? = null
    @JvmField
    @BindView(R.id.rv_feedback_type)
    var mRvType: RecyclerView? = null
    @JvmField
    @BindView(R.id.iv_feedback_pic)
    var mIvPic: ImageView? = null
    @JvmField
    @BindView(R.id.et_feedback_contact)
    var mEtContact: EditText? = null
    @JvmField
    @BindView(R.id.tv_feedback_commit)
    var mTvCommit: TextView? = null
    @JvmField
    @Autowired(name = "vodId")
    var mVodId: String? = ""

    private var mProgressDialog: MaterialDialog? = null

    private var mTypeAdapter: FeedbackTypeAdapter? = null

    override fun getContextViewId(): Int = R.layout.bean_activity_feedback


    override fun beforeSetContentView() {
        super.beforeSetContentView()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        mPresenter.getFeedbackTypes()
    }

    override fun setAdapter() {
        super.setAdapter()
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        mRvType?.layoutManager = layoutManager
        mTypeAdapter = FeedbackTypeAdapter(object : FeedbackTypeAdapter.OnChooseListener {
            override fun onChoose(type: FeedbackTypeBean) {
                mPresenter.type = type
            }
        })
        mRvType?.adapter = mTypeAdapter
    }

    override fun initView() {
        super.initView()
        mTvCount?.text = buildCountText(0)
    }

    override fun initToolBar() {
        super.initToolBar()
        findViewById<ImageButton>(R.id.ib_back_toolbar).visibility = View.VISIBLE
        val tvFeedbackHistory: TextView = findViewById(R.id.tv_editBtn_toolbar)
        tvFeedbackHistory.visibility = View.VISIBLE
        tvFeedbackHistory.setTextColor(Color.parseColor("#ffad43"))
        tvFeedbackHistory.text = "反馈历史"
        findViewById<TextView>(R.id.tv_title_toolbar).text = "用户反馈"

    }

    @OnTextChanged(R.id.et_feedback_content)
    fun onContentChanged(text: CharSequence) {
        mPresenter.content = text.toString()
        val length = text.length
        mTvCount?.text = buildCountText(length)
    }

    @OnTextChanged(R.id.et_feedback_contact)
    fun onContactChanged(text: CharSequence) {
        mPresenter.contact = text.toString()
    }

    /**
     * 获取输入的文字长度的彩色字符串
     */
    private fun buildCountText(count: Int): CharSequence {
        val builder = SpannableStringBuilder("${count}/150")
        builder.setSpan(ForegroundColorSpan(Color.parseColor("#ffad43")), 0, builder.length - 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return builder
    }

    @OnClick(R.id.ib_back_toolbar, R.id.tv_editBtn_toolbar, R.id.iv_feedback_pic, R.id.tv_feedback_commit)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ib_back_toolbar -> finish()
            R.id.tv_editBtn_toolbar -> {
                //跳转到历史纪录页面
                ARouter.getInstance().build("/feedback/history/activity").navigation()
            }
            R.id.iv_feedback_pic -> {
                //选择图片
                val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (EasyPermissions.hasPermissions(this, *perms)) {
                    openGallery()
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, *perms)
                }
            }
            R.id.tv_feedback_commit -> {
                //提交反馈
                mPresenter.commitFeedback(mVodId)
            }
        }
    }

    override fun showImage(imagePath: String?) {
        mIvPic?.let {
            GlideApp.with(this)
                    .load(imagePath)
                    .centerCrop()
                    .into(it)
        }
    }

    override fun setFeedbackTypes(list: List<FeedbackTypeBean>) {
        mTypeAdapter?.list = list
        mTypeAdapter?.notifyDataSetChanged()
    }

    override fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = MaterialDialog.Builder(Objects.requireNonNull(this))
                    .content(R.string.dialog_progress_waiting)
                    .progress(true, 0)
                    .canceledOnTouchOutside(false)
                    .build()
        }
        mProgressDialog!!.show()
    }

    override fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    /**
     * 打开相册
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, AppConstant.INTENT_CODE_IMAGE_GALLERY)
    }

    /**
     * 接收选择的图片
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_CODE_IMAGE_GALLERY) {
            try {
                mPresenter.getImageFromGallery(data)
            } catch (e: SecurityException) {
                ToastUtils.showToast(R.string.permission_sdcard_error)
            }
        }
    }

    override fun setCommitEnable(enable: Boolean) {
        mTvCommit?.isEnabled = enable
    }

    override fun showToast(toast: String) {
        ToastUtils.showToast(toast)
    }

    override fun close() {
        finish()
    }
}