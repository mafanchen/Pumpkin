package com.video.test.javabean

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(HotSearchWordJsonAdapter::class)
data class HotSearchWordListBean(
        val data: Map<String, List<String>>
)

private class HotSearchWordJsonAdapter : JsonDeserializer<HotSearchWordListBean> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): HotSearchWordListBean? {
        val map: HashMap<String, List<String>> = HashMap()
        if (json!!.isJsonObject) {
            val obj = json.asJsonObject
            val set = obj.entrySet()
            set.forEach { entry ->
                val key = entry.key
                val list = ArrayList<String>()
                val element = entry.value
                if (element.isJsonArray) {
                    val array = element.asJsonArray
                    array.forEach { e ->
                        if (e.isJsonObject) {
                            val o = e.asJsonObject
                            val name = o["vod_name"].asString
                            list.add(name)
                        }
                    }
                }
                map[key] = list
            }
        }
        return HotSearchWordListBean(map)
    }

}


