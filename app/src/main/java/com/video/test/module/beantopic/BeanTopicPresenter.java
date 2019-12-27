package com.video.test.module.beantopic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class BeanTopicPresenter extends BeanTopicContract.Presenter<BeanTopicModel> {
    private static final int THUMB_SIZE = 150;
    private IWXAPI mWxApi;
    private static final String TAG = "BeanTopicPresenter";

    @Override
    public void subscribe() {

    }


    @Override
    void getHomepageBeanTopicList() {
        Disposable disposable = mModel.getHomepageBeanTopicList(2)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> mView.showRefreshLayout())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(beanTopicListBean -> mView.hideRefreshLayout(true))
                .doOnError(e -> mView.hideRefreshLayout(false))
                .subscribe(beanTopicBeans -> mView.setHomepageBeanTopicList(beanTopicBeans.getVod()),
                        new RxExceptionHandler<>(throwable -> {
                            mView.showNetworkErrorView();
                            LogUtils.e(TAG, "getHomePageVideoList Error == " + throwable.getMessage());
                        }));
        addDisposable(disposable);
    }

    @Override
    void initWeChatApi() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
    }

    @Override
    void share2WeChat(Resources resources, String shareUrl, int targetSession) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject(shareUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = TestApp.getContext().getString(R.string.share_invite_title);
        message.description = TestApp.getContext().getString(R.string.share_invite_description);
        Bitmap logo = BitmapFactory.decodeResource(resources, R.mipmap.ic_logo);
        Bitmap thumbLogo = Bitmap.createScaledBitmap(logo, THUMB_SIZE, THUMB_SIZE, true);
        logo.recycle();
        message.thumbData = WeChatUtil.bmpToByteArray(thumbLogo, true);

        //构造一个 Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = targetSession;
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        }
        if (mWxApi.isWXAppInstalled()) {
            mWxApi.sendReq(req);
        } else {
            ToastUtils.showToast(TestApp.getContext(), "您还未安装微信");
        }

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
