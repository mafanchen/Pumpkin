package com.video.test.module.feedback

import android.content.Intent
import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.FeedbackTypeBean
import com.video.test.javabean.UploadAvatarBean
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FeedbackContract {

    interface View : IView {
        fun showProgressDialog()
        fun hideProgressDialog()
        fun showToast(toast: String)
        fun setFeedbackTypes(list: List<FeedbackTypeBean>)
        fun showImage(imagePath: String?)
        fun close()
        fun setCommitEnable(enable: Boolean)
    }

    interface Model : IModel {
        fun getFeedbackTypes(): Observable<List<FeedbackTypeBean>>
        fun uploadImage(tokenRequestBody: RequestBody, tokenIdRequestBody: RequestBody, imageBodyPart: MultipartBody.Part): Observable<UploadAvatarBean>
        fun commitFeedback(type: String, content: String, contact: String?, image: String?, vodId: String?, phoneInfo: String?): Observable<String>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        internal abstract var type: FeedbackTypeBean?
        internal abstract var content: String?
        internal abstract var contact: String?
        internal abstract fun getFeedbackTypes()
        internal abstract fun getImageFromGallery(intent: Intent?)
        internal abstract fun commitFeedback(vodId: String?)
    }
}