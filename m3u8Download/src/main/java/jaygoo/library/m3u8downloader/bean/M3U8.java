package jaygoo.library.m3u8downloader.bean;

import java.util.ArrayList;
import java.util.List;

import jaygoo.library.m3u8downloader.utils.MUtils;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2017/11/20
 * 描    述: m3u8实体类
 * ================================================
 */
public class M3U8 {

    private String videoId; //视频的 ID
    private String videoName; //视频的名称
    private String basePath;//去除后缀文件名的url
    private String m3u8FilePath;//m3u8索引文件路径
    private String dirFilePath;//切片文件目录
    private long fileSize;//切片文件总大小
    private long totalTime;//总时间，单位毫秒
    private String keyUri; // 秘钥uri地址
    private String keyName;
    private List<M3U8Ts> tsList = new ArrayList<M3U8Ts>();//视频切片


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getM3u8FilePath() {
        return m3u8FilePath;
    }

    public void setM3u8FilePath(String m3u8FilePath) {
        this.m3u8FilePath = m3u8FilePath;
    }

    public String getDirFilePath() {
        return dirFilePath;
    }

    public void setDirFilePath(String dirFilePath) {
        this.dirFilePath = dirFilePath;
    }

    public long getFileSize() {
        fileSize = 0;
        for (M3U8Ts m3U8Ts : tsList) {
            fileSize = fileSize + m3U8Ts.getFileSize();
        }
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFormatFileSize() {
        fileSize = getFileSize();
        if (fileSize == 0) {
            return "";
        }
        return MUtils.formatFileSize(fileSize);
    }

    public List<M3U8Ts> getTsList() {
        return tsList;
    }

    public void setTsList(List<M3U8Ts> tsList) {
        this.tsList = tsList;
    }

    public void addTs(M3U8Ts ts) {
        this.tsList.add(ts);
    }

    public long getTotalTime() {
        totalTime = 0;
        for (M3U8Ts m3U8Ts : tsList) {
            totalTime = totalTime + (int) (m3U8Ts.getSeconds() * 1000);
        }
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public String getKeyUri() {
        return keyUri;
    }

    public void setKeyUri(String keyUri) {
        this.keyUri = keyUri;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nvideoId: " + videoId);
        sb.append("\nvideoName: " + videoName);
        sb.append("\nbasePath: " + basePath);
        sb.append("\nm3u8FilePath: " + m3u8FilePath);
        sb.append("\ndirFilePath: " + dirFilePath);
        sb.append("\nfileSize: " + getFileSize());
        sb.append("\nfileFormatSize: " + MUtils.formatFileSize(fileSize));
        sb.append("\ntotalTime: " + totalTime);

   /*     for (M3U8Ts ts : tsList) {
            sb.append("\nts: " + ts);
        }*/

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof M3U8) {
            M3U8 m3U8 = (M3U8) obj;
            return basePath != null && basePath.equals(m3U8.basePath);
        }
        return false;
    }
}
