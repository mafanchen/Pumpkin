package com.video.test.module.feedback

import com.video.test.javabean.FeedbackTypeBean
import com.video.test.javabean.UploadAvatarBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FeedbackModel : FeedbackContract.Model {
    override fun getFeedbackTypes(): Observable<List<FeedbackTypeBean>> {
        return RetrofitHelper.getInstance()
                .getFeedbackTypes()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun uploadImage(tokenRequestBody: RequestBody, tokenIdRequestBody: RequestBody, imageBodyPart: MultipartBody.Part): Observable<UploadAvatarBean> {
        return RetrofitHelper.getInstance()
                .uploadAvatar(tokenRequestBody, tokenIdRequestBody, imageBodyPart)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun commitFeedback(type: String, content: String, contact: String?, image: String?): Observable<String> {
        return RetrofitHelper.getInstance()
                .commitFeedback(type, content, contact, image)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }
}