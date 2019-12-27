package com.video.test.module.share;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import com.ta.utdid2.device.UTDevice;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.ExchangeBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.ShareInfoBean;
import com.video.test.network.BaseException;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.DeviceHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SharePresenter extends ShareContract.Presenter<ShareModel> {
    private static final String TAG = "SharePresenter";
    private static final int THUMB_SIZE = 150;
    private int mRequestTimes = 0;

    private IWXAPI mWxApi;

    @Override
    public void subscribe() {

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


    @Override
    void getShareInfo() {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        Disposable disposable = mModel.getShareInfo(userToken, userTokenId)
                .subscribe(new Consumer<ShareInfoBean>() {
                    @Override
                    public void accept(ShareInfoBean shareInfo) {
                        mView.setShareInfo(shareInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.d(TAG, "getShareInfo Error == " + throwable.getMessage() + "requestTime : " + mRequestTimes);
                        if (throwable instanceof BaseException) {
                            int errorCode = ((BaseException) throwable).getErrorCode();
                            // 301 Token 失效的时候, 重新登陆申请一次 Token
                            if (AppConstant.REQUEST_INVALID_TOKEN == errorCode && mRequestTimes < AppConstant.REQUEST_LOGIN_MAXTIME) {
                                login();
                                mRequestTimes++;
                            }
                        }
                    }
                });
        addDisposable(disposable);
    }

    @Override
    void exchangeVip() {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        Disposable disposable = mModel.exchangeVip(userToken, userTokenId)
                .subscribe(new Consumer<ExchangeBean>() {
                    @Override
                    public void accept(ExchangeBean exchangeBean) {
                        LogUtils.d(TAG, "exchangeVip success == ");
                        mView.showGiftDialog(exchangeBean.getActivity());

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "exchangeVip Error == " + throwable.getMessage());
                        ToastUtils.showLongToast(TestApp.getContext(), throwable.getMessage());
                    }
                }));

        addDisposable(disposable);
    }

    @Override
    void login() {
//        String androidID = DeviceUtils.getAndroidID();
        String androidID = DeviceHelper.getDeviceId(TestApp.getContext());
        String utdid = UTDevice.getUtdid(TestApp.getContext());
        String androidId = Settings.Secure.getString(TestApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        LogUtils.d(TAG, "utdid : " + utdid + "Android Id " + androidId);

        Disposable disposable = mModel.login(utdid, androidID, "")
                .subscribe(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        LogUtils.i(TAG, "login success");
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN, loginBean.getToken());
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, loginBean.getToken_id());
                        SpUtils.putString(TestApp.getContext(), AppConstant.SERVER_VERSION_CODE, loginBean.getServerCode());
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.USER_IS_LOGIN, true);
                        //只有登录成功了 获取了正常的 Token 和 Token ID  之后再重新请求一次分享的信息
                        getShareInfo();
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(TAG, "login Error == " + throwable.getMessage() + "requestTime : " + mRequestTimes);
                        if (throwable instanceof BaseException) {
                            int errorCode = ((BaseException) throwable).getErrorCode();
                            // 301 Token 失效的时候,重新申请一次 Token
                            if (AppConstant.REQUEST_INVALID_TOKEN == errorCode && mRequestTimes < AppConstant.REQUEST_LOGIN_MAXTIME) {
                                login();
                                mRequestTimes++;
                            }
                        }
                    }
                }));
        addDisposable(disposable);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
