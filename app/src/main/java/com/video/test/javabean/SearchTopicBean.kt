package com.video.test.javabean

class SearchTopicBean(
        topic: BeanTopicContentBean,
        val isShowTitle: Boolean = false
) : BeanTopicContentBean() {
    init {
        id = topic.id
        zt_f_pic = topic.zt_f_pic
        zt_title = topic.zt_title
        zt_pic = topic.zt_pic
        zt_content = topic.zt_content
        zt_num = topic.zt_num
    }
}