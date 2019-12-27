package com.video.test.utils.cast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.InteractiveAdListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.browse.api.AdInfo;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.IQRCodeListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.video.test.module.player.PlayerActivity;
import com.video.test.utils.LogUtils;

import java.util.List;

/**
 * Created by Zippo on 2018/10/13.
 * Date: 2018/10/13
 * Time: 17:08:24
 */
public class LelinkHelper {

    private static final String TAG = "LelinkHelper";

    private static final String APP_ID = "10803";
    private static final String APP_SECRET = "f68f53bea884fefe5fe62ee795a156c7";

    private static LelinkHelper sLelinkHelper;
    private Context mContext;
    private UIHandler mUIHandler;
    private AllCast mAllCast;
    // 数据
    private List<LelinkServiceInfo> mInfos;
    private AdInfo mAdInfo;

    // 监听器
    private IConnectListener mActivityConnectListener;
    private IBrowseListener mBrowseListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            LogUtils.d(TAG, "onSuccess size:" + (list == null ? 0 : list.size()));
            mInfos = list;
            if (resultCode == IBrowseListener.BROWSE_SUCCESS) {
                LogUtils.d(TAG, "browse success");
                StringBuffer buffer = new StringBuffer();
                if (null != mInfos) {
                    for (LelinkServiceInfo info : mInfos) {
                        buffer.append("name：").append(info.getName())
                                .append(" uid: ").append(info.getUid())
                                .append(" type:").append(info.getTypes()).append("\n");
                    }
                    buffer.append("---------------------------\n");
                    if (null != mUIHandler) {
                        // 发送文本信息
                        mUIHandler.sendMessage(buildTextMessage(buffer.toString()));
                        if (mInfos.isEmpty()) {
                            mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_SEARCH_NO_RESULT));
                        } else {
                            mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_SEARCH_SUCCESS));
                        }
                    }
                }
            } else {
                if (null != mUIHandler) {
                    // 发送文本信息
                    LogUtils.d(TAG, "browse error:Auth error");
                    mUIHandler.sendMessage(buildTextMessage("搜索错误：Auth错误"));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_SEARCH_ERROR));
                }
            }

        }

    };
    private IConnectListener mConnectListener = new IConnectListener() {

        @Override
        public void onConnect(final LelinkServiceInfo serviceInfo, final int extra) {
            LogUtils.d(TAG, "onConnect:" + serviceInfo.getName());
            if (null != mUIHandler) {
                String type = extra == TYPE_LELINK ? "Lelink" : extra == TYPE_DLNA ? "DLNA" : extra == TYPE_NEW_LELINK ? "NEW_LELINK" : "IM";
                String text;
                if (TextUtils.isEmpty(serviceInfo.getName())) {
                    text = "pin码连接" + type + "成功";
                } else {
                    text = serviceInfo.getName() + "连接" + type + "成功";
                }
                mUIHandler.sendMessage(buildTextMessage(text));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_CONNECT_SUCCESS, text));
            }
            mUIHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mActivityConnectListener != null) {
                        mActivityConnectListener.onConnect(serviceInfo, extra);
                    }
                }

            });
        }

        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
            LogUtils.d(TAG, "onDisconnect:" + serviceInfo.getName() + " disConnectType:" + what + " extra:" + extra);
            if (what == IConnectListener.CONNECT_INFO_DISCONNECT) {
                if (null != mUIHandler) {
                    String text;
                    if (TextUtils.isEmpty(serviceInfo.getName())) {
                        text = "pin码连接断开";
                    } else {
                        text = serviceInfo.getName() + "连接断开";
                    }
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_DISCONNECT, text));
                }
            } else if (what == IConnectListener.CONNECT_ERROR_FAILED) {
                String text = null;
                if (extra == IConnectListener.CONNECT_ERROR_IO) {
                    text = serviceInfo.getName() + "连接失败";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_WAITTING) {
                    text = serviceInfo.getName() + "等待确认";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_REJECT) {
                    text = serviceInfo.getName() + "连接拒绝";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_TIMEOUT) {
                    text = serviceInfo.getName() + "连接超时";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_BLACKLIST) {
                    text = serviceInfo.getName() + "连接黑名单";
                }
                if (null != mUIHandler) {
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_CONNECT_FAILURE, text));
                }
            }
            if (mActivityConnectListener != null) {
                mActivityConnectListener.onDisconnect(serviceInfo, what, extra);
            }
        }

    };
    private ILelinkPlayerListener mPlayerListener = new ILelinkPlayerListener() {

        @Override
        public void onLoading() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("开始加载"));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_LOADING));
            }
        }

        @Override
        public void onStart() {
            LogUtils.d(TAG, "onStart:");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("开始播放"));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_PLAY));
            }
        }

        @Override
        public void onPause() {
            LogUtils.d(TAG, "onPause");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("暂停播放"));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_PAUSE));
            }
        }

        @Override
        public void onCompletion() {
            LogUtils.d(TAG, "onCompletion");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("播放完成"));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_COMPLETION));
            }
        }

        @Override
        public void onStop() {
            LogUtils.d(TAG, "onStop");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("播放结束"));
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_STOP));
            }
        }

        @Override
        public void onSeekComplete(int pPosition) {
            LogUtils.d(TAG, "onSeekComplete position:" + pPosition);
            mUIHandler.sendMessage(buildTextMessage("设置进度"));
            mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_SEEK));
        }

        @Override
        public void onInfo(int what, int extra) {
            LogUtils.d(TAG, "onInfo what:" + what + " extra:" + extra);
        }

        @Override
        public void onError(int what, int extra) {
            LogUtils.d(TAG, "onError what:" + what + " extra:" + extra);
            String text = null;
            if (what == ILelinkPlayerListener.PUSH_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.PUSH_ERRROR_FILE_NOT_EXISTED) {
                    text = "文件不存在";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_OFFLINE) {
                    text = "IM TV不在线";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IMAGE) {

                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_UNSUPPORTED_MIMETYPE) {
                    text = "IM不支持的媒体类型";
                } else {
                    text = "未知";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_UNSUPPORTED) {
                    text = "不支持镜像";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_REJECT_PERMISSION) {
                    text = "镜像权限拒绝";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_DEVICE_UNSUPPORTED) {
                    text = "设备不支持镜像";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_PREPARE) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_INFO) {
                    text = "获取镜像信息出错";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_PORT) {
                    text = "获取镜像端口出错";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_INPUT_SCREENCODE, text));
                    return;
                } else if (extra == ILelinkPlayerListener.GRAP_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PLAY) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "播放无响应";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_INPUT_SCREENCODE, text));
                    return;
                } else if (extra == ILelinkPlayerListener.RELEVANCE_DATA_UNSUPPORTED) {
                    text = "老乐联不支持数据透传,请升级接收端的版本！";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.RELEVANCE_DATA_UNSUPPORT, text));
                    return;
                } else if (extra == ILelinkPlayerListener.GRAP_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_STOP) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "退出 播放无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PAUSE) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "暂停无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_RESUME) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "恢复无响应";
                }
            }
            mUIHandler.sendMessage(buildTextMessage(text));
            mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_PLAY_ERROR, text));
        }

        /**
         * 音量变化回调
         *
         * @param percent 当前音量
         */
        @Override
        public void onVolumeChanged(float percent) {
            LogUtils.d(TAG, "onVolumeChanged percent:" + percent);
        }

        /**
         * 进度更新回调
         *
         * @param duration 媒体资源总长度
         * @param position 当前进度
         */
        @Override
        public void onPositionUpdate(long duration, long position) {
            LogUtils.d(TAG, "onPositionUpdate duration:" + duration + " position:" + position);
            long[] arr = new long[]{duration, position};
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildStateMessage(IUIUpdateListener.STATE_POSITION_UPDATE, arr));
            }
        }

    };
    private InteractiveAdListener mDemoAdListener = new InteractiveAdListener() {

        @Override
        public void onAdLoaded(AdInfo adInfo) {
            LogUtils.i(TAG, "onAdLoaded:" + adInfo);
            mAdInfo = adInfo;
        }

    };

    private LelinkHelper(Context context) {
        mContext = context;
        mUIHandler = new UIHandler(Looper.getMainLooper());
        mAllCast = new AllCast(context.getApplicationContext(), APP_ID, APP_SECRET);
        mAllCast.setOnBrowseListener(mBrowseListener);
        mAllCast.setConnectListener(mConnectListener);
        mAllCast.setPlayerListener(mPlayerListener);
    }

    public static LelinkHelper getInstance(Context context) {
        if (sLelinkHelper == null) {
            sLelinkHelper = new LelinkHelper(context);
        }
        return sLelinkHelper;
    }

    public void setUIUpdateListener(IUIUpdateListener listener) {
        mUIHandler.setUIUpdateListener(listener);
    }

    public void setActivityConenctListener(IConnectListener listener) {
        this.mActivityConnectListener = listener;
    }

    public List<LelinkServiceInfo> getInfos() {
        return mInfos;
    }

    public List<LelinkServiceInfo> getConnectInfos() {
        return mAllCast.getConnectInfos();
    }

    public void addQRServiceInfo(String qrCode, IQRCodeListener listener) {
        mAllCast.addQRServiceInfo(qrCode, listener);
    }

    public void addPinCodeServiceInfo(String pinCode) {
        mAllCast.addPinCodeServiceInfo(pinCode);
    }

    public void onBrowseListGone() {
        mAllCast.onBrowseListGone();
    }

    public void onPushButtonClick() {
        mAllCast.onPushButtonClick();
    }

    public void browse(int type) {
        mAllCast.browse(type);
    }

    public void stopBrowse() {
        mAllCast.stopBrowse();
    }

    public void connect(LelinkServiceInfo info) {
        if (null != mUIHandler) {
            mUIHandler.sendMessage(buildTextMessage("选中了:" + info.getName()
                    + " type:" + info.getTypes()));
        }
        mAllCast.connect(info);
    }

    public void disConnect(LelinkServiceInfo info) {
        mAllCast.disConnect(info);
    }

    public void deleteRemoteServiceInfo(LelinkServiceInfo selectInfo) {
        mAllCast.deleteRemoteServiceInfo(selectInfo);
    }

    public boolean canPlayLocalVideo(LelinkServiceInfo info) {
        return mAllCast.canPlayLocalVideo(info);
    }

    public boolean canPlayLocalPhoto(LelinkServiceInfo info) {
        return mAllCast.canPlayLocalPhoto(info);
    }

    public boolean canPlayLocalAudio(LelinkServiceInfo info) {
        return mAllCast.canPlayLocalAudio(info);
    }

    public boolean canPlayOnlineVideo(LelinkServiceInfo info) {
        return mAllCast.canPlayOnlineVideo(info);
    }

    public boolean canPlayOnlineAudio(LelinkServiceInfo info) {
        return mAllCast.canPlayOnlineAudio(info);
    }

    public boolean canPlayOnlinePhoto(LelinkServiceInfo info) {
        return mAllCast.canPlayOnlinePhoto(info);
    }

    public void playLocalMedia(String url, int mediaType, String screencode) {
        mAllCast.playLocalMedia(url, mediaType, screencode);
    }

    public void playNetMedia(String url, int mediaType, String screencode) {
        mAllCast.playNetMedia(url, mediaType, screencode);
    }

    public void resume() {
        mAllCast.resume();
    }

    public void release() {
        mAllCast.release();
    }

    public void pause() {
        mAllCast.pause();
    }

    public void stop() {
        mAllCast.stop();
    }

    public void seekTo(int progress) {
        mAllCast.seekTo(progress);
    }

    public void setVolume(int percent) {
        mAllCast.setVolume(percent);
    }

    public void voulumeUp() {
        mAllCast.voulumeUp();
    }

    public void voulumeDown() {
        mAllCast.voulumeDown();
    }

    public void setInteractiveAdListener() {
        mAllCast.setInteractiveAdListener(mDemoAdListener);
    }

    public void sendRelevantInfo(String appid, boolean isSdk) {
        mAllCast.sendRelevantInfo(appid, isSdk);
    }

    public void sendLeboRelevantInfo(String appid, boolean isSdk) {
        mAllCast.sendLeboRelevantInfo(appid, isSdk);
    }

    public void sendRelevantErrorInfo() {
        mAllCast.sendRelevantErrorInfo();
    }

    public void sendRelevanceAppInfo() {
        mAllCast.sendRelevantAppInfo();
    }

    public void playNetMediaAndPassthHeader(String url, int type) {
        mAllCast.playNetMediaWithHeader(url, type);
    }

    public void playNetMediaAndPassthMediaAsset(String url, int type) {
        mAllCast.playNetMediaWithAsset(url, type);
    }

    public void startWithLoopMode(String url, boolean isLocalFile) {
        mAllCast.startWithLoopMode(url, isLocalFile);
    }

    public void startNetVideoWith3rdMonitor(String netVideoUrl) {
        mAllCast.startNetVideoWith3rdMonitor(netVideoUrl);
    }

    public void onInteractiveAdShow() {
        mAllCast.onInteractiveAdShow(mAdInfo, LelinkPlayer.STATUS_SUCCESS);
    }

    public void onInteractiveAdClosed() {
        mAllCast.onInteractiveAdClosed(mAdInfo, 10, LelinkPlayer.STATUS_SUCCESS);
    }

    public void startMirror(PlayerActivity activity, LelinkServiceInfo info, int resolutionLevel,
                            int bitrateLevel, boolean audioEnable, String screencode) {
        mAllCast.startMirror(activity, info, resolutionLevel, bitrateLevel, audioEnable, screencode);
    }

    public void stopMirror() {
        mAllCast.stopMirror();
    }

    private Message buildTextMessage(String text) {
        Message message = Message.obtain();
        message.what = UIHandler.MSG_TEXT;
        message.obj = text;
        return message;
    }

    private Message buildStateMessage(int state) {
        return buildStateMessage(state, null);
    }

    private Message buildStateMessage(int state, Object object) {
        Message message = Message.obtain();
        message.what = UIHandler.MSG_STATE;
        message.arg1 = state;
        if (null != object) {
            message.obj = object;
        }
        return message;
    }

    private static class UIHandler extends Handler {

        private static final int MSG_TEXT = 1;
        private static final int MSG_STATE = 2;
        private IUIUpdateListener mUIUpdateListener;

        private UIHandler(Looper pMainLooper) {
            super(pMainLooper);
        }

        private void setUIUpdateListener(IUIUpdateListener pUIUpdateListener) {
            mUIUpdateListener = pUIUpdateListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TEXT:
                    String text = (String) msg.obj;
                    if (null != mUIUpdateListener) {
                        mUIUpdateListener.onUpdateText(text);
                    }
                    break;
                case MSG_STATE:
                    int state = msg.arg1;
                    Object obj = msg.obj;
                    if (null != mUIUpdateListener) {
                        mUIUpdateListener.onUpdateState(state, obj);
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
