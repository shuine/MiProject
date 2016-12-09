package com.xiaomi.client;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.xiaomi.client.download.FileDownloadQueueManager;

/**
 * @author yeshuxin on 16-10-24.
 */

public class ClientApplication extends Application {

    public static ClientApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58098b52");
        FileDownloadQueueManager.getQueueInstance().init(this);
    }
}
