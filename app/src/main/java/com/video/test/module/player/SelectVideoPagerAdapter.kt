package com.video.test.module.player

import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.flyco.tablayout.listener.CustomTabEntity
import com.video.test.javabean.PlayerUrlListBean
import com.video.test.javabean.VideoPlayTabBean

class SelectVideoPagerAdapter(val presenter: PlayerContract.Presenter<*>) : PagerAdapter() {
    var videoList: List<PlayerUrlListBean>? = null
    private var tabList: List<CustomTabEntity>? = null
    private val map: HashMap<CustomTabEntity, PlayerSelectItemAdapter> = HashMap()
    //    private var mCurrentBean: PlayerUrlListBean? = null
    private var mCurrentVideoPosition = -1

    override fun isViewFromObject(view: View, any: Any): Boolean = view == any

    override fun getCount(): Int = if (tabList == null) 0 else tabList!!.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bean = tabList!![position]
        val rv = RecyclerView(container.context)
        container.addView(rv)
//        rv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val selectGridLayoutManager = GridLayoutManager(container.context, 3)
        rv.layoutManager = selectGridLayoutManager
        val adapter = PlayerSelectItemAdapter(presenter)
        adapter.setData(videoList)
        adapter.setHistoryPosition(mCurrentVideoPosition)
        adapter.setCurrentData((bean as VideoPlayTabBean).data)
        map[bean] = adapter
        rv.adapter = adapter
        return rv
    }

    override fun getItemPosition(any: Any): Int {
        //使notifyDataSetChange方法强制刷新，解决切换视频分集视图不改变
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View?)
    }

    fun setCurrentVideoPosition(position: Int) {
        mCurrentVideoPosition = position
        map.forEach { entry ->
            entry.value.setHistoryPosition(position)
            entry.value.notifyDataSetChanged()
        }
    }

    fun setTabList(tabList: List<CustomTabEntity>) {
        map.clear()
        this.tabList = tabList
    }

}