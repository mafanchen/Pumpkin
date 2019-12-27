package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Enoch Created on 2018/7/27.
 */
public class VideoPlayerBean {

    /**
     * list : [{"vod_id":"30519","vod_name":"延禧攻略","vod_scroe":"0.0","vod_keywords":"爱情,古装","vod_addtime":"2018-07-27","vod_area":"大陆","vod_actor":"秦岚,聂远,佘诗曼,吴谨言","vod_director":"惠楷栋,温德光","vod_use_content":"乾隆六年，少女魏璎珞为寻求长姐死亡真相，入紫禁城为宫女。经调查，璎珞证实姐姐之死与荒唐王爷弘昼有关，立志要讨回公道。富察皇后娴于礼法，担心璎珞走上歧途，竭力给予她温暖与帮助。在皇后的悉心教导下，魏璎珞一步步成长为正直坚强的宫廷女官，并放下怨恨、认真生活。皇后不幸崩逝，令璎珞对乾隆误会重重，二人从互相敌视到最终彼此理解、互相扶持。璎珞凭勇往直前的勇气、机敏灵活的头脑、宽广博大的胸怀，化解宫廷上下的重重困难，最终成为襄助乾隆盛世的令贵妃。直到璎珞去世前，她才将当年富察皇后临终托付告知乾隆，即望她陪伴弘历身边，辅助他做一个有为明君，乾隆终知富察氏用心良苦。乾隆六十年，乾隆帝宣示魏璎珞之子嘉亲王永琰为皇太子，同时追封皇太子生母令懿皇贵妃为孝仪皇后，璎珞终于用自己的一生，实现了对富察皇后的承诺。","vod_urlArr":[{"vod_key":1,"vod_val":"ffhd://426213860/4SAWOGIAAAAAA2VZT6USGQYC3LMA3KOMJJVK56WVXZ2DC/TIHXBLVERDICP5H5RZ6FFK75PFPJEMYF/延禧攻略EP01.mp4"},{"vod_key":2,"vod_val":"ffhd://393479506/KICXIFYAAAAABF2ISKMY2VWKC25LNRU6L7JPN34T275PW/X27WVK2UFJ4XZF25IYGJRUFKYJM2LRTS/延禧攻略EP02.mp4"},{"vod_key":3,"vod_val":"ffhd://424369186/EJOEWGIAAAAAAT53B6GBMGUDFP2HBTMEB4ZOUDO356S6A/PGW3VZ3EGHQVOH7GYYH3PE5CMXNV7MSQ/延禧攻略EP03.mp4"},{"vod_key":4,"vod_val":"ffhd://396777834/NJM2MFYAAAAAAATGP4JEKSZ2LH7POB475TURPTO4FPYVS/F6FCOBNLRFJOB2NYFJMFUM4S4SZEPM3L/延禧攻略EP04.mp4"},{"vod_key":5,"vod_val":"ffhd://408598570/FK4FUGAAAAAAB5NCUVJVM65GUU2LYBWDCWCB4CQ5HBPYW/IXBJNUZBLKCUZX7BUFJGMUFCJJ7YESVF/延禧攻略EP05.mp4"},{"vod_key":6,"vod_val":"ffhd://406681757/TV4D2GAAAAAAB3QLD6WJITBKQL2MJ63HSC2URSN6D37EC/GIWCDPAVLZU2I6D4ERLCPZCPMDV5DXGS/延禧攻略EP06.mp4"},{"vod_key":7,"vod_val":"ffhd://405007811/YPWSGGAAAAAABWGNWUKU6ILN6HECHQPFG6HYMOGWZNMJ6/LMTWZYRW2CCUQDVAVKBOQCNWNBCQCR7Y/延禧攻略EP07.mp4"},{"vod_key":8,"vod_val":"ffhd://405497396/GRTCWGAAAAAABM5B6QGL6DN2N5FGF56WQYWJMFXGBTJ2G/N2LYGT3BW4LX7QXG6PZS4CSMPXG5W4NI/延禧攻略EP08.mp4"},{"vod_key":9,"vod_val":"ffhd://383428636/DSUNUFQAAAAAA5FYA4ZQURRCCX3LZONIG6XVW64D3ZJWW/I4WPGMWHG5GC36WUEWWJGBH4XPHDVZPG/延禧攻略EP09.mp4"},{"vod_key":10,"vod_val":"ffhd://407473748/KSHESGAAAAAAAXJ6UYS46OTSPCKBKRFGOPXIMQ7DL5GOA/2K4S2QDFZIEXIIP4KPXXNSZ7E3F7GI6F/延禧攻略EP10.mp4"},{"vod_key":11,"vod_val":"ffhd://416002823/A6Z4WGAAAAAABIAREWLHXNG2CO7WVEQP5IHDC5N7PTKHQ/VZNQWWDSY6QXVEPLB6EHRJAQOYVCEK26/延禧攻略EP11.mp4"},{"vod_key":12,"vod_val":"ffhd://421486783/X5QB6GIAAAAAAZ4LQW2DPRAZNFQWDYFA5R5SBGGK4QBLW/N2L3UYHO32RSAG3NX6Y3UZ4FTC2VULEL/延禧攻略EP12.mp4"},{"vod_key":13,"vod_val":"ffhd://399094235/3OY4SFYAAAAABLBIGME37LN55M7OHADL3WTJX567GH2ZI/FGHHMRASV2ASFH5DOHYPVHCKBSFL2PRJ/延禧攻略EP13.mp4"},{"vod_key":14,"vod_val":"ffhd://413865568/MALKWGAAAAAAAH3HELQNZ4LXXCRWWIULFCLK7BETQT2UW/DKJ4KJ4JXUA3XKVH5COQYMCH277NBPXT/延禧攻略EP14.mp4"},{"vod_key":15,"vod_val":"ffhd://401140061/LXU6QFYAAAAABKGUMGGOLK5MAHWFI7XLLCYQAMA537XP6/S7BK5DBD5KJLLFRFIGAOBVIOWA55GV5B/延禧攻略EP15.mp4"},{"vod_key":16,"vod_val":"ffhd://393163751/44ZW6FYAAAAAAX3ORM7WISEMYLJ7C4BBVLTYNSCHC6NZK/UORXRT2BHKGV7BP7R4EVN2TC7ZJJUOEK/延禧攻略EP16.mp4"},{"vod_key":17,"vod_val":"ffhd://407132442/DJMUIGAAAAAAALPWF3WKCGJ4SVJ6DCFXCXDXYVCZ6KLAW/GAHNWDB3PFCVW3UJOS6ZFIXLBVMINWYK/延禧攻略EP17.mp4"},{"vod_key":18,"vod_val":"ffhd://411853449/RFRIYGAAAAAAA3JGA645IEZQFL5KMOYSHSA5BRGR2TZMM/BDW52UWNLA2PBR66UGCPAR3GJJTEZZV4/延禧攻略EP18.mp4"}]}]
     * recommenData : [{"vod_id":"30443","vod_name":"岁岁年年柿柿红","vod_pic":"http://www.ffpic.net/vod/2018-07/5b44b5a25f179.jpg","vod_scroe":"0.0"},{"vod_id":"30579","vod_name":"甜蜜暴击","vod_pic":"http://www.ffpic.net/vod/2018-07/5b55dad91a900.jpg","vod_scroe":"0.0"},{"vod_id":"30332","vod_name":"陪读妈妈","vod_pic":"http://www.ffpic.net/vod/2018-07/5b39f7cf1fcb3.jpg","vod_scroe":"0.0"},{"vod_id":"30497","vod_name":"开封府传奇","vod_pic":"http://www.ffpic.net/vod/2018-07/5b4dec7222a83.jpg","vod_scroe":"0.0"},{"vod_id":"30385","vod_name":"猎毒人","vod_pic":"http://www.ffpic.net/vod/2018-07/5b3f6a107fb49.jpg","vod_scroe":"0.0"},{"vod_id":"30616","vod_name":"合伙人","vod_pic":"http://www.ffpic.net/vod/2018-07/5b5ab4c6b9e99.jpg","vod_scroe":"0.0"}]
     * vod_length : 0
     * is_collect : 0
     * collect_id : 0
     */

    private int vod_length;
    private int is_collect;
    private String collect_id;
    private String unVip;
    private String unReg;
    private List<PlayerVideoListBean> list;
    private List<PlayerRecommendListBean> recommenData;

    //是否有专题
    @SerializedName("is_zt")
    private boolean isZt;
    @SerializedName("zt_info")
    private List<BeanTopicContentBean> ztInfo;

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

    public int getVod_length() {
        return vod_length;
    }

    public void setVod_length(int vod_length) {
        this.vod_length = vod_length;
    }

    public int getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(int is_collect) {
        this.is_collect = is_collect;
    }

    public String getCollect_id() {
        return collect_id;
    }

    public void setCollect_id(String collect_id) {
        this.collect_id = collect_id;
    }

    public String getUnVip() {
        return unVip;
    }

    public void setUnVip(String unVip) {
        this.unVip = unVip;
    }

    public String getUnReg() {
        return unReg;
    }

    public void setUnReg(String unReg) {
        this.unReg = unReg;
    }

    public List<PlayerVideoListBean> getList() {
        return list;
    }

    public void setList(List<PlayerVideoListBean> list) {
        this.list = list;
    }

    public List<PlayerRecommendListBean> getRecommenData() {
        return recommenData;
    }

    public void setRecommenData(List<PlayerRecommendListBean> recommenData) {
        this.recommenData = recommenData;
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

    public boolean isZt() {
        return isZt;
    }

    public void setZt(boolean zt) {
        isZt = zt;
    }

    public List<BeanTopicContentBean> getZtInfo() {
        return ztInfo;
    }

    public void setZtInfo(List<BeanTopicContentBean> ztInfo) {
        this.ztInfo = ztInfo;
    }
}
