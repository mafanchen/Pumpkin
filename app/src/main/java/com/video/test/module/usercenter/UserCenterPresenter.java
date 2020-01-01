package com.video.test.module.usercenter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.video.test.javabean.AdInfoBean;
import com.video.test.javabean.ButtonBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.UploadAvatarBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.network.BaseException;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.DeviceHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PathUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class UserCenterPresenter extends UserCenterContract.Presenter<UserCenterModel> {

    private static final int THUMB_SIZE = 150;
    private static final String TAG = "UserCenterPresenter";
    private IWXAPI mWxApi;
    private int mRequestTimes = 0;


    @Override
    public void subscribe() {

    }

    @Override
    void getUserInfo() {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        Disposable disposable = mModel.getUserInfo(userToken, userTokenId)
                .subscribe(new Consumer<UserCenterBean>() {
                    @Override
                    public void accept(UserCenterBean userCenterBean) {
                        mView.setUserInfo(userCenterBean);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getUserInfo Error == " + throwable.getMessage() + "requestTime : " + mRequestTimes);
                        if (throwable instanceof BaseException) {
                            int errorCode = ((BaseException) throwable).getErrorCode();
                            // 301 Token 失效的时候, 重新登陆申请一次 Token
                            if (AppConstant.REQUEST_INVALID_TOKEN == errorCode && mRequestTimes < AppConstant.REQUEST_LOGIN_MAXTIME) {
                                login();
                                mRequestTimes++;
                            }
                        }
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void uploadAvatar(File imageFile) {
        mView.showProgressDialog();
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        RequestBody tokenRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), userToken);
        RequestBody tokenIdRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), userTokenId);
        RequestBody avatarRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part avatarBodyPart = MultipartBody.Part.createFormData("pic", imageFile.getName(), avatarRequestBody);

        Disposable disposable = mModel.uploadAvatar(tokenRequestBody, tokenIdRequestBody, avatarBodyPart)
                .subscribe(new Consumer<UploadAvatarBean>() {
                    @Override
                    public void accept(UploadAvatarBean uploadAvatarBean) {
                        mView.getUploadAvatarMessage(uploadAvatarBean);
                        mView.hideProgressDialog();
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "uploadAvatar Error == " + throwable.getMessage());
                        mView.hideProgressDialog();
                    }
                }));
        addDisposable(disposable);

    }

    @Override
    void getImageFromGallery(Intent intent) {
        if (null != intent) {
            Uri imageUri = intent.getData();
            String imagePath = PathUtils.getAbsolutePath(TestApp.getContext(), imageUri);
            LogUtils.i(getClass(), "getImageFromGallery imagePath == " + imagePath);
            if (null != imagePath) {
                File imageFile = new File(imagePath);
                uploadAvatar(imageFile);
            }
        }
    }

    @Override
    void getVersionInfo() {
        Disposable disposable = mModel.getVersionInfo()
                .subscribe(new Consumer<VersionInfoBean.InfoBean>() {
                    @Override
                    public void accept(VersionInfoBean.InfoBean infoBean) {
                        LogUtils.d(TAG, "getVersionInfo success == " + infoBean.toString());
                        mView.setVersionInfo(infoBean);

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getVersionInfo Error == " + throwable.getMessage());
                    }
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
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.USER_IS_LOGIN, true);
                        SpUtils.putString(TestApp.getContext(), AppConstant.SERVER_VERSION_CODE, loginBean.getServerCode());
                        //只有登录成功了 获取了正常的 Token 和 Token ID 才会请求一次用户信息
                        getUserInfo();
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

    @Override
    void setAvatarUrl(String avatarUrl) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.setAvatarUrl(userToken, userTokenId, avatarUrl)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        mView.getSetAvatarUrl(s);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "setAvatarUrl Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 如果已登录,隐藏游客信息
     *
     * @param group
     */
    @Override
    void hideUserInfo(Group group) {
        if (null != group) {
            group.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 如果已登录,显示注册用户信息
     */
    @Override
    void showVisitorInfo(TextView tvLoginBtn) {
        if (null != tvLoginBtn) {
            tvLoginBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 未登录状态,显示游客的信息
     */
    @Override
    void showUserInfo(Group group) {
        if (null != group) {
            group.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 未登录状态, 隐藏注册用户的信息
     */

    @Override
    void hideVisitorInfo(TextView tvLoginBtn) {
        if (null != tvLoginBtn) {
            tvLoginBtn.setVisibility(View.INVISIBLE);
        }
    }

    void getAdInfo() {
        Disposable subscribe = mModel.getUserCenterAdInfo()
                .subscribe(
                        adBean -> mView.setAdInfo(adBean),
                        new RxExceptionHandler<>(throwable -> Log.e(TAG, "get ad error," + throwable.getMessage()))
                );
        addDisposable(subscribe);
    }
}
