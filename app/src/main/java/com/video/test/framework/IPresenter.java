package com.video.test.framework;

/**
 * Created by Enoch on 2017/5/9.
 */

public interface IPresenter<V extends IView> {

    void subscribe();

    void unSubscribe();

    void attachView(V view);

}
