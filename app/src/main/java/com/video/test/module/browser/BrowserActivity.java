package com.video.test.module.browser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

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
    @BindView(R.id.tv_url)
    TextView mTvUrl;
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
            mTvUrl.setText(mWebUrl);
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
            mWebView.setWebViewClient(new BeanWebViewClient(this));
            mWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
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

    private static class BeanWebViewClient extends WebViewClient {
        private Activity mActivity;

        public BeanWebViewClient(BrowserActivity browserActivity) {
            this.mActivity = browserActivity;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            String url = null;
//            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                url = request.getUrl().toString();
//            } else {
//                url = request.toString();
//            }
//            LogUtils.d("shouldOverrideUrlLoading", "url = " + url);
//            view.loadUrl(url);
//            return false;
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
