package com.xiaomi.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.iflytek.cloud.SpeechRecognizer;

public class MainActivity extends Activity {

    private ImageView mImageView;
    private Button mBtn;
    private Button mBtnDownload;
    private SpeechRecognizer mIat;
    private String url = "http://heze.dzwww.com/tyyl/201609/W020160909524017295763.png";
    private String TAG = "miVoice";
    private StringBuilder result = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnDownload = (Button) findViewById(R.id.home_tv);
        mBtn = (Button) findViewById(R.id.home_btn);
        btnClick();
    }

    private void btnClick(){
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startDialogSpeech();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,VoiceActivity.class);
                startActivity(intent);
            }
        });

        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,DownloadActivity.class);
                startActivity(intent);
            }
        });
    }

}
