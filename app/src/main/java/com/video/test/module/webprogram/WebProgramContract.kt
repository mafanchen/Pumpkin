package com.video.test.module.webprogram

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView

/**
 * @author Enoch Created on 2019-12-13.
 */
interface WebProgramContract {
    interface View : IView {

        fun closeWeb()

    }

    interface Model : IModel {

    }

    abstract class Persenter<M : Model> : BasePresenter<M, View>() {

    }
}