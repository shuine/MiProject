package com.xiaomi.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.xiaomi.client.download.FileInfo;
import com.xiaomi.client.presenter.DownloadPresenter;
import com.xiaomi.client.util.ToastUtil;
import com.xiaomi.client.view.IDownloadView;

import java.io.File;

/**
 * @author yeshuxin on 16-10-24.
 */

public class DownloadActivity extends Activity implements IDownloadView{

    private Button mStartDownload;
    private DownloadPresenter mPresenter;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/mivoice/";
    private String url = "http://speed.myzone.cn/pc_elive_1.1.rar";
    private String aurl = "http://s17.mogucdn.com/new1/v1/bmisc/73eab2358df2e856847d7116670e5305/142833944464.zip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mPresenter = new DownloadPresenter(this,this);
        initView();
    }

    private void initView(){
        mStartDownload = (Button) findViewById(R.id.btn_download);
        mStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInfo info = new FileInfo();
                info.setTag("downloadFileTest");
                info.setUrl(aurl);
                info.setFileName("miTest.rar");
                info.setPat(path);
                mPresenter.startDownload(info);
                mStartDownload.setEnabled(false);
            }
        });
    }

    @Override
    public void updateProgress(Long length, Long totalLength) {

    }

    @Override
    public void downloadComplete(String msg,String path) {
        mStartDownload.setEnabled(true);
        ToastUtil.show(msg);
    }

    @Override
    public void downloadError(String msg) {
        mStartDownload.setEnabled(true);
        ToastUtil.show("Error:" + msg);
    }
}
