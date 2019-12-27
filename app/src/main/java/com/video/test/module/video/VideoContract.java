package com.video.test.module.video;

import android.content.res.Resources;
import android.support.v4.app.Fragment;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.HotSearchWordListBean;
import com.video.test.javabean.ScreenBean;

import java.util.ArrayList;

import io.reactivex.Observable;


/**
 * @author Enoch Created on 2018/6/27.
 */
public interface VideoContract {

    interface Model extends IModel {

        Observable<ScreenBean> getScreenTypes();

        Observable<HotSearchWordListBean> getHotSearchWord();
    }

    interface View extends IView {


        void setScreenTypes(ScreenBean screenBean);

        void showHistoryLayout();

        void initHistoryLayout(String name, String time, String vodId);

        void hideHistoryLayout();

        void setHotSearchWord(String searchWord);

        Fragment getCurrentFragment();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {


        abstract ArrayList<Fragment> initFragmentList();

        abstract void initWeChatApi();

        abstract void share2WeChat(Resources resources, String shareUrl, int targetSession);

        abstract void getScreenTypes();

        abstract void getHotSearchWord();

    }
}
