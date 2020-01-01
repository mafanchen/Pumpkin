package com.video.test.module.beantopic;

import com.video.test.javabean.BeanTopicListBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class BeanTopicModel implements BeanTopicContract.Model {

    @Override
    public Observable<BeanTopicListBean> getHomepageBeanTopicList(int pid, int order) {
        return RetrofitHelper.getInstance()
                .getHomepageBeanTopic(pid, order)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
