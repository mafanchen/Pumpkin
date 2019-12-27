package com.video.test.module.download

import android.text.TextUtils
import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class DownloadVideoListModel : DownloadVideoListContract.Model {

    override fun getDownloadingTask(vodId: String): Observable<List<M3U8DownloadBean>> =
            Observable.create<List<M3U8DownloadBean>> { emitter ->
                emitter.onNext(DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadingTasks().filter { TextUtils.equals(it.videoId, vodId) })
                emitter.onComplete()
            }.compose(RxSchedulers.io_main())


    override fun getDownloadedTask(vodId: String): Observable<List<M3U8DownloadBean>> =
            Observable.create<List<M3U8DownloadBean>> { emitter ->
                emitter.onNext(DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadedTasks().filter { TextUtils.equals(it.videoId, vodId) })
                emitter.onComplete()
            }.compose(RxSchedulers.io_main())

    override fun getAllTask(vodId: String): Observable<List<M3U8DownloadBean>> =
            Observable.create<List<M3U8DownloadBean>> { emitter ->
                emitter.onNext(DBManager.getInstance(TestApp.getContext()).queryM3U8TasksByVideoId(vodId))
                emitter.onComplete()
            }.compose(RxSchedulers.io_main())
}