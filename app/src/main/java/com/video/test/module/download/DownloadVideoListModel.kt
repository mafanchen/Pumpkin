package com.video.test.module.download

import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class DownloadVideoListModel : DownloadVideoListContract.Model {

    override fun getDownloadingTask(): Observable<List<M3U8DownloadBean>> =
            Observable.create<List<M3U8DownloadBean>> { emitter ->
                emitter.onNext(DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadingTasks())
                emitter.onComplete()
            }.compose(RxSchedulers.io_main())


    override fun getDownloadedTask(): Observable<List<M3U8DownloadBean>> =
            Observable.create<List<M3U8DownloadBean>> { emitter ->
                emitter.onNext(DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadedTasks())
                emitter.onComplete()
            }.compose(RxSchedulers.io_main())
}