package com.video.test.ui.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.utils.LogUtils;
import com.video.test.utils.SDCardUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 分享图片的dialog
 *
 * @author : AhhhhDong
 * @date : 2019/5/13 17:39
 */
public class ShareImageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ShareImageDialogFragment";
    private static final int THUMB_SIZE = 150;

    private String mImagePath;
    private String mCoverPath;
    private boolean mIsGif;


    private ImageView mIvPreview;
    private TextView mTvFriends;
    private IWXAPI mWxApi;


    private OnDismissListener mOnDismissListener;
    private OnSaveImageListener mOnSaveImageListener;

    public static ShareImageDialogFragment newInstance(String path, String cover, boolean isGif) {
        ShareImageDialogFragment fragment = new ShareImageDialogFragment();
        fragment.mImagePath = path;
        fragment.mIsGif = isGif;
        fragment.mCoverPath = cover;
        return fragment;
    }

    public static ShareImageDialogFragment newInstance(String path) {
        ShareImageDialogFragment fragment = new ShareImageDialogFragment();
        fragment.mImagePath = path;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.bean_fragment_share_image, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().setCanceledOnTouchOutside(true);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initWXApi();
        //gif不分享朋友圈
        if (mIsGif && mTvFriends != null) {
            mTvFriends.setVisibility(View.GONE);
        }
        if (mImagePath != null && mIvPreview != null) {
            GlideApp.with(this)
                    .load(mImagePath)
                    .centerCrop()
                    .into(mIvPreview);
        }

    }

    private void initWXApi() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
    }

    private void initView() {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView mTvCancel = view.findViewById(R.id.tv_cancel);
        mIvPreview = view.findViewById(R.id.iv_preview);
        TextView mTvWx = view.findViewById(R.id.tv_share_wx);
        mTvFriends = view.findViewById(R.id.tv_share_friends);
        TextView mTvSave = view.findViewById(R.id.tv_save);
        mTvCancel.setOnClickListener(this);
        mTvWx.setOnClickListener(this);
        mTvFriends.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                LogUtils.d(TAG, "cancel click");
                dismiss();
                break;
            case R.id.tv_share_wx:
                LogUtils.d(TAG, "wx click");
                if (mIsGif) {
                    shareGif(AppConstant.WX_SCENE_SESSION);
                } else {
                    shareImage(AppConstant.WX_SCENE_SESSION);
                }
                break;
            case R.id.tv_share_friends:
                LogUtils.d(TAG, "friends click");
                if (mIsGif) {
                    shareGif(AppConstant.WX_SCENE_TIMELINE);
                } else {
                    shareImage(AppConstant.WX_SCENE_TIMELINE);
                }
                break;
            case R.id.tv_save:
                LogUtils.d(TAG, "save click");
                save();
                break;
            default:
                break;
        }
    }

    private void shareGif(int scene) {
        WXEmojiObject object = new WXEmojiObject();
        object.emojiPath = mImagePath;
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = object;
        message.title = getString(R.string.share_image_title);
//        message.description = "我分享了一张图片";
        Bitmap thumb = BitmapFactory.decodeFile(mCoverPath);
        message.thumbData = WeChatUtil.bmpToByteArray(thumb, true, 32);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("emoji");
        req.message = message;
        req.scene = scene;
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        }
        if (mWxApi.isWXAppInstalled()) {
            mWxApi.sendReq(req);
        } else {
            ToastUtils.showToast("您还未安装微信");
        }
    }

    private void shareImage(int scene) {
        WXImageObject object = new WXImageObject();
        object.imagePath = mImagePath;
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = object;
        message.title = getString(R.string.share_image_title);
//        message.description = "我分享了一张图片";
//        Bitmap logo = BitmapFactory.decodeResource(TestApp.getContext().getResources(), R.mipmap.ic_logo);
//        Bitmap thumbLogo = Bitmap.createScaledBitmap(logo, THUMB_SIZE, THUMB_SIZE, true);
//        logo.recycle();
//        message.thumbData = WeChatUtil.bmpToByteArray(thumbLogo, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("image");
        req.message = message;
        req.scene = scene;
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        }
        if (mWxApi.isWXAppInstalled()) {
            mWxApi.sendReq(req);
        } else {
            ToastUtils.showToast("您还未安装微信");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void save() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File inFile = new File(mImagePath);
            String savePath = SDCardUtils.getSDRootPath() + File.separator + "DCIM";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            File outFile = new File(saveDir, inFile.getName());
            //            File outFile = new File(DownloadUtil.getRootDirFile(), inFile.getName());
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            byte[] buffer = new byte[1444];
            fis = new FileInputStream(inFile);
            fos = new FileOutputStream(outFile);
            int length;
            while ((length = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            ToastUtils.showToast("已保存文件至" + outFile.getAbsolutePath());
            if (mOnSaveImageListener != null) {
                mOnSaveImageListener.onSaveImage(outFile);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public void setSaveImageListener(OnSaveImageListener onSaveImageListener) {
        mOnSaveImageListener = onSaveImageListener;
    }

    public interface OnSaveImageListener {
        void onSaveImage(File file);
    }
}
