package com.video.test.module.localplayer;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.ui.widget.LocalLandVideoPlayer;

import java.io.File;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface LocalPlayerContract {

    interface Model extends IModel {

    }

    interface View extends IView {

        void showShareImageDialog(File file, File cover, boolean isGif);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void initVideoPlayer(LocalLandVideoPlayer videoPlayer);

        abstract void initVideoInfo(LocalLandVideoPlayer videoPlayer, String videoName, String videoUrl);

        abstract void initWeChatApi();
    }
}
