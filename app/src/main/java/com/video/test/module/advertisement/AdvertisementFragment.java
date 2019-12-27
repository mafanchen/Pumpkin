package com.video.test.module.advertisement;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.ui.base.BaseLazyFragment;

/**
 * 广告模块暂未开启
 *
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/advertisement/fragment")
public class AdvertisementFragment extends BaseLazyFragment<AdvertisementPresenter> implements AdvertisementContract.View {


    public static Fragment newInstance(String title) {
        AdvertisementFragment advertisementFragment = new AdvertisementFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        advertisementFragment.setArguments(bundle);
        return advertisementFragment;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getContentViewId() {
        return 0;
    }


}
