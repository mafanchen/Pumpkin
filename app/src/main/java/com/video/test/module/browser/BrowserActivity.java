package com.video.test.module.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/browser/activity")
public class BrowserActivity extends BaseActivity<BrowserPresenter> implements BrowserContract.View {
    private static final String TAG = "PurchaseActivity";

    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.wv_browser_activity)
    WebView mWebView;
    @Autowired
    String mWebUrl;
    @Autowired
    boolean mIsFromSplash;


    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_browser;
    }

    @Override
    protected void initToolBar() {
        if (null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (!TextUtils.isEmpty(mWebUrl)) {
            initWebView();
        } else {
            skipSplashActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != mWebView) {
                mWebView.removeAllViews();
                mWebView.setWebViewClient(null);
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initWebView() {
        if (null != mWebView) {
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setSupportZoom(true);
            settings.setDefaultTextEncodingName("UTF-8");
            //这里防止https网页中的http图片打不开
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            settings.setBlockNetworkImage(false);
            mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            });
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(mWebUrl);
        } else {

        }
    }


    @OnClick(R.id.ib_back_toolbar)
    void clickBack() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (null != mWebView && mWebView.canGoBack()) {
            mWebView.goBack();
        } else if (mIsFromSplash) {
            skipSplashActivity();
        } else {
            super.onBackPressed();
        }
    }

    public void skipSplashActivity() {
        boolean userIsLogin = SpUtils.getBoolean(TestApp.getContext(), "userIsLogin", false);
        if (userIsLogin) {
            ARouter.getInstance().build("/homepage/activity").navigation();
            finish();
        } else {
            ARouter.getInstance().build("/homepage/activity").navigation();
            finish();
        }
    }
}
