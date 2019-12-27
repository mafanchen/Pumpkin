package com.video.test.javabean;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Enoch Created on 2018/7/26.
 */
public class HomepageVideoBean {


    /**
     * pid : 1
     * tag : 0
     * type : 热门抢先
     * list : [{"vod_id":"33","vod_pic":"http://www.ffpic.net/vod/2018-04/5ad429a637bc1.jpg","vod_name":"极限挑战第四季","vod_scroe":"8.0","vod_keywords":"真人秀","vod_area":"大陆"},{"vod_id":"409","vod_pic":"http://www.ffpic.net/vod/2018-04/5ad822598fbc8.jpg","vod_name":"奔跑吧第二季","vod_scroe":"5.9","vod_keywords":"真人秀","vod_area":"大陆"},{"vod_id":"3674","vod_pic":"http://www.ffpic.net/vod/2017-10/59f4ab5da1b8a.jpg","vod_name":"战狼2","vod_scroe":"7.2","vod_keywords":"动作","vod_area":"大陆"},{"vod_id":"312","vod_pic":"http://www.ffpic.net/vod/2018-03/5abf2f646a2f3.jpg","vod_name":"头号玩家","vod_scroe":"8.8","vod_keywords":"动作,科幻,冒险","vod_area":"美国"},{"vod_id":"26239","vod_pic":"http://www.ffpic.net/vod/2016-04/5714e928ab731.jpg","vod_name":"千与千寻","vod_scroe":"9.3","vod_keywords":"奇幻,冒险,动画","vod_area":"日本"},{"vod_id":"1409","vod_pic":"http://www.ffpic.net/vod/2017-12/5a33e7328c532.jpg","vod_name":"寻梦环游记","vod_scroe":"9.1","vod_keywords":"喜剧,冒险,动画","vod_area":"美国"}]
     */

    private int pid;
    private String tag;
    private String type;
    @JsonAdapter(VideoJsonAdapter.class)
    private List<Object> list;
    @SerializedName("type_pic")
    private String typePic;
    /**
     * 当大于0是竖着显示的video
     */
    @SerializedName("parent_id")
    private String parentId;
    /**
     * 是否是广告位
     */
    @SerializedName("is_ad")
    private boolean isAd;
    /**
     * 广告
     */
    @SerializedName("ad_info")
    private AdInfoBean adInfo;

    @SerializedName("show_id")
    private String showId;

    public String getTypePic() {
        return typePic;
    }

    public void setTypePic(String typePic) {
        this.typePic = typePic;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isAd() {
        return isAd;
    }

    public void setAd(boolean ad) {
        isAd = ad;
    }

    public AdInfoBean getAdInfo() {
        return adInfo;
    }

    public void setAdInfo(AdInfoBean adInfo) {
        this.adInfo = adInfo;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    static class VideoJsonAdapter implements JsonDeserializer<List<Object>> {

        @Override
        public ArrayList<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            if (json == null || !json.isJsonArray()) {
                return null;
            }
            ArrayList<Object> list = new ArrayList<>();
            JsonArray array = json.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement element = array.get(i);
                JsonObject object = element.getAsJsonObject();
                JsonElement parentId = object.get("parent_id");
                //判断是否有parent_id字段，确认是横向还是纵向视频
                if (parentId != null && parentId.isJsonPrimitive() && parentId.getAsInt() > 0) {
                    //横向推荐视频
                    list.add(context.deserialize(element, VideoRecommendBean.class));
                } else {
                    //普通分类视频
                    list.add(context.deserialize(element, VideoBean.class));
                }
            }
            return list;
        }
    }
}
