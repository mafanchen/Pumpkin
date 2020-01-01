package com.video.test.module.feedback

import android.content.Intent
import android.text.TextUtils
import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.javabean.FeedbackTypeBean
import com.video.test.javabean.UploadAvatarBean
import com.video.test.network.RxExceptionHandler
import com.video.test.sp.SpUtils
import com.video.test.utils.LogUtils
import com.video.test.utils.PathUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FeedbackPresenter : FeedbackContract.Presenter<FeedbackModel>() {

    companion object {
        const val TAG = "FeedbackPresenter"
    }

    /**
     * 用来存已经上传的图片的地址,避免重复上传
     * key: 本地图片路径
     * value: 网络图片路径
     */
    private val mImageMap = HashMap<String, String>()

    /**
     * 上传图片的本地路径
     */
    private var imagePath: String? = null
    /**
     * 输入的文字
     */
    override var content: String? = null
    /**
     * 输入的联系方式
     */
    override var contact: String? = null
    /**
     * 已经选择的
     */
    override var type: FeedbackTypeBean? = null

    override fun subscribe() {

    }

    /**
     * 选择图片后，保存图片地址到缓存
     */
    override fun getImageFromGallery(intent: Intent?) {
        if (null != intent) {
            val imageUri = intent.data
            imagePath = PathUtils.getAbsolutePath(TestApp.getContext(), imageUri)
            mView.showImage(imagePath)
            LogUtils.i(TAG, "getImageFromGallery imagePath == $imagePath")
        }
    }

    private fun uploadImage(imagePath: String) {
        val imageFile = File(imagePath)
        val userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no")
        val userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no")
        val tokenRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), userToken)
        val tokenIdRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), userTokenId)
        val avatarRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
        val avatarBodyPart = MultipartBody.Part.createFormData("pic", imageFile.name, avatarRequestBody)
        val disposable = mModel.uploadImage(tokenRequestBody, tokenIdRequestBody, avatarBodyPart)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.showProgressDialog() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<UploadAvatarBean> { uploadAvatarBean ->
                    val picUrl = uploadAvatarBean.url_pic
                    mImageMap[imagePath] = picUrl
                    //TODO vodID 以及 phoneInfo 还未实现
                    commit(type!!.id, content!!, contact, picUrl, "", "")
                }, RxExceptionHandler<Throwable>(Consumer { throwable ->
                    LogUtils.e(TAG, "uploadImage Error == " + throwable.message)
                    mView.hideProgressDialog()
                }))
        addDisposable(disposable)
    }

    override fun commitFeedback() = when {
        type == null -> mView.showToast("请选择反馈类型")
        TextUtils.isEmpty(content) -> mView.showToast("请输入反馈详情")
        content!!.length <= 2 -> mView.showToast("请输入不少于2个字符")
        TextUtils.isEmpty(imagePath) -> {
            //TODO vodID and phoneInfo 未实现
            commit(type!!.id, content!!, contact, null, "", "")
        }
        else -> {
            //这里通过一个map来存已经上传的图片路径，避免反馈接口报错后，点击提交导致同一张图片上传两次
            val netWorkPath = mImageMap[imagePath]
            if (netWorkPath != null) {
                //TODO vodID and phoneInfo 未实现
                commit(type!!.id, content!!, contact, netWorkPath, "", "")
            } else {
                uploadImage(imagePath!!)
            }
        }
    }

    override fun getFeedbackTypes() {
        val subscribe = mModel.getFeedbackTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    mView.setFeedbackTypes(it)
                }, RxExceptionHandler { LogUtils.e(TAG, "getFeedbackTypes Error == " + it.message) })
        addDisposable(subscribe)
    }

    /**
     * 调用反馈接口
     */
    private fun commit(type: String, content: String, contact: String?, imageUrl: String?, vodId: String?, phoneInfo: String?) {
        val subscribe = mModel.commitFeedback(type, content, contact, imageUrl, vodId, phoneInfo)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.showProgressDialog() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    mView.hideProgressDialog()
                    mView.showToast("提交反馈成功")
                    mView.close()
                }, RxExceptionHandler {
                    mView.hideProgressDialog()
                    LogUtils.e(TAG, "commitFeedback Error == " + it.message)
                })
        addDisposable(subscribe)
    }
}