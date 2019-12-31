package com.video.test.module.download

import android.content.Context
import com.video.test.db.DBManager
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.javabean.SdCardInfoBean
import com.video.test.utils.RxSchedulers
import com.video.test.utils.SDCardUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe

class DownloadModel : DownloadContract.Model {

    override fun getSDCardInfo(): Observable<SdCardInfoBean> {
        return Observable.create(ObservableOnSubscribe<SdCardInfoBean> { emitter ->
            val freeSpaceBytes = SDCardUtils.getFreeSpaceBytes(SDCardUtils.getSDRootPath())
            val totalSpaceBytes = SDCardUtils.getTotalSpaceBytes(SDCardUtils.getSDRootPath())
            emitter.onNext(SdCardInfoBean(freeSpaceBytes, totalSpaceBytes))
            emitter.onComplete()
        }).compose(RxSchedulers.io_main())
    }

    override fun getAllTasks(context: Context): Observable<MutableList<M3U8DownloadBean>> {
        return Observable.create { emitter ->
            val tasks = DBManager.getInstance(context).queryM3U8Tasks()
            emitter.onNext(tasks)
            emitter.onComplete()
        }
    }
}