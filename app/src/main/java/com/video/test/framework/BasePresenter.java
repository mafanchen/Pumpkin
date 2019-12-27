package com.video.test.framework;

import com.video.test.utils.ClazzUtils;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Created by Enoch on 2017/5/8.
 */

public abstract class BasePresenter<M, V extends IView> implements IPresenter<V> {

    protected M mModel;

    protected V mView;

    private CompositeDisposable composite = new CompositeDisposable();

    /**
     * 在构造方法中，只用获取BasePresenter的第一个泛型参数的类
     */
    public BasePresenter() {
        mModel = ClazzUtils.getGenericInstance(this, 0);
    }

    /**
     * 使Presenter对象持有view的引用
     */
    @Override
    public void attachView(V view) {
        mView = view;
    }

    /**
     * 清除所有的订阅，防止内存泄漏
     */
    @Override
    public void unSubscribe() {
        LogUtils.d(getClass().getSimpleName(), "unSubscribe  == ");
        composite.clear();
    }

    /**
     * 添加订阅
     */
    public boolean addDisposable(Disposable d) {
        LogUtils.d(getClass().getSimpleName(), "addDisposable  == ");

        return composite.add(d);
    }

}
