package com.video.test.module.about;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface AboutContarct {

    interface Model extends IModel {

    }

    interface View extends IView {

    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

    }


}
