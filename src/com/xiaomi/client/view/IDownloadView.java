package com.xiaomi.client.view;

/**
 * @author yeshuxin on 16-10-24.
 */

public interface IDownloadView {
    void updateProgress(Long length,Long totalLength);
    void downloadComplete(String msg,String path);
    void downloadError(String msg);
}
