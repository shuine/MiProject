package com.xiaomi.client.presenter;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xiaomi.client.download.FileDownloadQueueManager;
import com.xiaomi.client.download.FileInfo;
import com.xiaomi.client.view.IDownloadView;

import java.io.File;
import java.io.IOException;

/**
 * @author yeshuxin on 16-10-24.
 */

public class DownloadPresenter {
    private Context mContext;
    private IDownloadView mDownloadView;

    public DownloadPresenter(Context context,IDownloadView downloadView){
        mContext = context;
        mDownloadView = downloadView;
    }

    public void startDownload(final FileInfo info){

        if(info == null && TextUtils.isEmpty(info.getUrl())){
            return;
        }
        if(TextUtils.isEmpty(info.getFileName()) || TextUtils.isEmpty(info.getPath())){
            return;
        }
        File tempFile = new File (info.getPath() + info.getFileName());
        if(tempFile.exists()){
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadView.downloadError("文件创建失败");
            return;
        }
        FileDownloadQueueManager.getQueueInstance().downLoadFile(info.getTag(), info.getUrl(), null,
                info.getPath()+info.getFileName(), null, new Response.Listener<File>() {
                    @Override
                    public void onStart() {
                    //    mDownloadView.downloadComplete(info.getPath());
                    }

                    @Override
                    public void onSuccess(File response, boolean isResponseFromCache) {

                        mDownloadView.downloadComplete("下载成功",info.getPath());
                    }

                    @Override
                    public void onError(VolleyError error) {
                        mDownloadView.downloadComplete("下载失败",info.getPath());
                    }

                    @Override
                    public void onFinish() {
                        mDownloadView.downloadComplete("下载完成",info.getPath());
                    }
                });

    }
}
