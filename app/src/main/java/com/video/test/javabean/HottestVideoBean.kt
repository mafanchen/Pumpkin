package com.video.test.javabean

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

@JsonAdapter(HottestVideoBean.HottestVideoBeanJsonAdapter::class)
data class HottestVideoBean(
        val id: String?,
        /**
         * 所属频道id
         */
        val showId: String?
) : VideoBean() {
    class HottestVideoBeanJsonAdapter : TypeAdapter<HottestVideoBean>() {
        override fun read(reader: JsonReader?): HottestVideoBean {
            var id: String? = null
            var showId: String? = null
            var vodId: String? = null
            var name: String? = null
            var pic: String? = null
            var score: String? = null
            var vodContinue: String? = null
            if (reader != null) {
                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "id" -> id = reader.nextString()
                        "show_id" -> showId = reader.nextString()
                        "vod_id" -> vodId = reader.nextString()
                        "d_name" -> name = reader.nextString()
                        "vod_pic" -> pic = reader.nextString()
                        "d_score" -> score = reader.nextString()
                        "vod_continu" -> vodContinue = reader.nextString()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
            }
            val hottestVideoBean = HottestVideoBean(id, showId)
            hottestVideoBean.vod_id = vodId
            hottestVideoBean.vod_name = name
            hottestVideoBean.vod_pic = pic
            hottestVideoBean.vod_scroe = score
            hottestVideoBean.vod_continu = vodContinue
            return hottestVideoBean
        }

        override fun write(out: JsonWriter?, value: HottestVideoBean?) {
            if (out != null && value != null) {
                out.beginObject()
                out.name("id").value(value.id)
                out.name("show_id").value(value.showId)
                out.name("vod_id").value(value.vod_id)
                out.name("d_name").value(value.vod_name)
                out.name("vod_pic").value(value.vod_pic)
                out.name("d_score").value(value.vod_scroe)
                out.name("vod_continu").value(value.vod_continu)
                out.endObject()
            }
        }

    }
}