package com.video.test.module.swap;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;

public interface SwapContract {
    interface Model extends IModel {
    }

    interface View extends IView {
        void setShareNum(int shareNum);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<M, View> {
    }
}
