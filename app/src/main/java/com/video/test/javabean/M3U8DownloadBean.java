package com.video.test.javabean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

/**
 * @author Enoch Created on 2018/12/20.
 */
@Entity
public class M3U8DownloadBean {
    /**
     * 主键
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 视频的资源ID
     */
    @NotNull
    private String videoId;
    /**
     * 视频的名称,分集名称
     */
    @NotNull
    private String videoName;

    /**
     * 整个剧集的名称，区别于{@link #videoName}
     */
    @NotNull
    private String videoTotalName;

    /**
     * 下载的地址
     */
    @NotNull
    private String videoUrl;
    /**
     * 是否已完成下载
     */
    private boolean isDownloaded;
    /**
     * 视频状态
     */
    private int taskStatus;
    /**
     * 视频总时长
     */
    private long totalTime;
    /**
     * 视频文件总大小
     */
    private long totalFileSize;
    /**
     * 视频 m3u8保存路径
     */
    private String m3u8FilePath;
    /**
     * 切片文件缓存目录路径
     */
    private String dirFilePath;
    /**
     * 当前正在下载的当前切片编号
     */
    private int curTs;
    /**
     * 所有切片的总个数
     */
    private int totalTs;

    /**
     * 当前百分比进度
     */
    private float progress;

    @Transient
    private boolean isSelect;


    @Generated(hash = 1487939430)
    public M3U8DownloadBean(Long id, @NotNull String videoId, @NotNull String videoName,
            @NotNull String videoTotalName, @NotNull String videoUrl, boolean isDownloaded,
            int taskStatus, long totalTime, long totalFileSize, String m3u8FilePath, String dirFilePath,
            int curTs, int totalTs, float progress) {
        this.id = id;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoTotalName = videoTotalName;
        this.videoUrl = videoUrl;
        this.isDownloaded = isDownloaded;
        this.taskStatus = taskStatus;
        this.totalTime = totalTime;
        this.totalFileSize = totalFileSize;
        this.m3u8FilePath = m3u8FilePath;
        this.dirFilePath = dirFilePath;
        this.curTs = curTs;
        this.totalTs = totalTs;
        this.progress = progress;
    }

    @Generated(hash = 350342533)
    public M3U8DownloadBean() {
    }


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String voidId) {
        this.videoId = voidId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
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

    public int getCurTs() {
        return curTs;
    }

    public void setCurTs(int curTs) {
        this.curTs = curTs;
    }

    public int getTotalTs() {
        return totalTs;
    }

    public void setTotalTs(int totalTs) {
        this.totalTs = totalTs;
    }

    public boolean getIsDownloaded() {
        return this.isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getVideoTotalName() {
        return videoTotalName;
    }

    public void setVideoTotalName(String videoTotalName) {
        this.videoTotalName = videoTotalName;
    }
}
