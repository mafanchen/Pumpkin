package com.video.test.module.setting;

import android.content.Context;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.ui.widget.SwitchButton;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface SettingContract {

    interface Model extends IModel {


    }

    interface View extends IView {
        void clearVideoCacheSuccess();

    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void clearAllHistory();

        abstract void showCleanHistoryDialog(Context context);

        abstract void showCleanVideoCacheDialog(Context context);

        protected abstract void getSwitchButtonStatus(SwitchButton switchButton);

        public abstract void getSwitchHistoryButtonStatus(SwitchButton switchButton);

        public abstract void getSwitchPushButtonStatus(SwitchButton switchButton);
    }


}
