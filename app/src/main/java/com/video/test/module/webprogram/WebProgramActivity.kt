package com.video.test.module.webprogram

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.BindView
import butterknife.OnClick
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.R
import com.video.test.ui.base.BaseActivity
import com.video.test.utils.ToastUtils

/**
 * @author Enoch Created on 2019-12-13.
 */

@Route(path = "/webProgram/activity")
class WebProgramActivity : BaseActivity<WebProgramPresenter>(), WebProgramContract.View {

    @JvmField
    @BindView(R.id.wv_webProgram)
    var mWebView: WebView? = null

    @JvmField
    @Autowired(name = "webUrl")
    var mWebUrl: String? = null
    var catchHomeKey = false

    companion object {
        private const val TAG = "WebProgramActivity"
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        ARouter.getInstance().inject(this)
    }

    override fun getContextViewId(): Int {
        return R.layout.bean_activity_webprogram
    }


    override fun initData() {

    }

    override fun initView() {
        if (!TextUtils.isEmpty(mWebUrl)) {
            initWebView()
        }
    }

    private fun initWebView() {
        val settings = mWebView!!.settings
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true)
        settings.defaultTextEncodingName = "UTF-8"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        settings.blockNetworkLoads = false
        mWebView!!.setDownloadListener { url: String?, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        mWebView!!.webViewClient = object : WebViewClient() {}
        mWebView!!.loadUrl(mWebUrl)
    }


    override fun onResume() {
        super.onResume()
        mWebView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mWebView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.clearCache(true)
        mWebView?.stopLoading()
        mWebView?.removeAllViews()
        mWebView?.destroy()
        mWebView = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.i(TAG, "Click Home Button")

        }
        return super.onKeyDown(keyCode, event)
    }


    @OnClick(R.id.iv_edit_webProgram, R.id.iv_close_webProgram)
    fun onViewClick(view: View) {
        when (view.id) {
            R.id.iv_edit_webProgram -> {
                ToastUtils.showToast("该功能暂未开放")
            }
            R.id.iv_close_webProgram -> {
                closeWeb()
            }
        }
    }

    override fun onBackPressed() {
        if (mWebView != null && mWebView!!.canGoBack()) {
            mWebView!!.goBack()
        }
    }


    override fun closeWeb() {
        finish()
    }

}