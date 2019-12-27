package com.video.test.module.swap;

import com.video.test.javabean.event.SwapEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SwapPresenter extends SwapContract.Presenter<SwapModel> {
    private static final String TAG = "SwapPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    public void attachView(SwapContract.View view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setInviteCount(SwapEvent count) {
        mView.setShareNum(count.getInviteCount());
    }


}
