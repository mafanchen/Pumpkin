package com.video.test.module.videotype

import com.video.test.R


/**
 * @author : AhhhhDong
 * @date : 2019/4/11 17:06
 */
class VideoTypeListFragment : BaseVideoTypeListFragment<VideoTypeListPresenter>() {

    companion object {
        fun newInstance(pid: Int, adType: Int): VideoTypeListFragment {
            val fragment = VideoTypeListFragment()
            fragment.pid = pid
            fragment.adType = adType
            return fragment
        }
    }

    override fun initViewBeforeLoadData() {
        mBanner = view!!.findViewById(R.id.banner_videoList_fragment)
        mSwipeRefresh = view?.findViewById(R.id.refresh_videoList_Fragment)
        mRvVideoList = view?.findViewById(R.id.rv_videoList_Fragment)
        mLoadingView = view?.findViewById(R.id.loadingView)
    }

    override fun getContentViewId(): Int {
        return R.layout.bean_fragment_video_list
    }

}