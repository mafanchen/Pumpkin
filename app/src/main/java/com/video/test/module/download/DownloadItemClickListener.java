package com.video.test.module.download;

import com.video.test.javabean.M3U8DownloadBean;

/**
 * @author Enoch Created on 2018/12/21.
 */
public interface DownloadItemClickListener {

    void onItemSelected(boolean isSelected, M3U8DownloadBean bean);

    void startTask(M3U8DownloadBean downloadingBean, int position);

    void pauseTask(M3U8DownloadBean downloadingBean, int position);

    void downloadingTask(M3U8DownloadBean downloadingBean, int position);

    void playNetworkVideo(String videoId, String videoUrl);

    void playLocalVideo(String localUrl, String localName);

}
