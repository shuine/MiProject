package com.xiaomi.client.presenter;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.xiaomi.client.util.ExpressionUtils;
import com.xiaomi.client.util.JsonParser;
import com.xiaomi.client.view.IHomeView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.tokenizer.ParseException;


/**
 * @author yeshuxin on 16-10-24.
 */

public class SpeechPresenter implements RecognizerDialogListener{

    private Context mcontext;
    private IHomeView mHomeView;
    private SpeechRecognizer mIat;
    private StringBuilder result = new StringBuilder();

    private String rightSpeech = "回答正确";
    private String wrongSpeech = "回答错误";
    private double diff = 0.00001;
    private boolean mCalculate = false;

    public SpeechPresenter(Context context,IHomeView homeView){
        mcontext = context;
        mHomeView = homeView;
        initSpeech();
    }

    private void initSpeech(){
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat= SpeechRecognizer.createRecognizer(mcontext, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

    }

    public void startSpeechListening(){
        if(mIat != null) {
            mIat.startListening(mRecoListener);
        }
    }

    public void stopSpeechListening(){
        if(mIat != null) {
            mIat.stopListening();
        }
    }

    public void startDialogSpeech(boolean isCalculate){
        result.delete(0,result.length());
        mCalculate = isCalculate;
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        RecognizerDialog iatDialog = new RecognizerDialog(mcontext,null);
        //2.设置听写参数，同上节
        iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //3.设置回调接口
        iatDialog.setListener(this);
        //4.开始听写
        iatDialog.show();
    }

    public void startSpeechVoice(String speech) {

        if(TextUtils.isEmpty(speech)){
            return;
        }
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(mcontext, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(speech, mSynListener);
        //合成监听器
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        if(!b){
            result.append(JsonParser.parseIatResult(recognizerResult.getResultString()));
        }else {
            mHomeView.OnSpeechResult(result.toString(),mCalculate);
           /* mHomeView.onCaculateResult(parseInputContent(result.toString()));*/
          //  caculateExpression(result.toString(),true);
        }
    }

    @Override
    public void onError(SpeechError speechError) {

    }

    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
        }

        @Override
        public void onBeginOfSpeech() {
            result.delete(0,result.length());
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            if(!b){
                result.append(JsonParser.parseIatResult(recognizerResult.getResultString()));
            }else {
                mHomeView.OnSpeechResult(result.toString(),mCalculate);

              /*  mHomeView.onCaculateResult(parseInputContent(result.toString()));*/
                //caculateExpression(result.toString(),true);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void caculateExpression(String expression,boolean isVoice){
       /* if(isVoice) {
            mHomeView.OnSpeechResult(expression);
        }*/
        double calResult = parseInputContent(expression);
        mHomeView.onCaculateResult(calResult);
        //startSpeechVoice(String.valueOf(calResult));
    }

    private double parseInputContent(String content){
        double exResult = 0.0;
        content = ExpressionUtils.excludeInvalidChar(content);
        if(ExpressionUtils.isValidExpression(content)){
            Scope scope = Scope.create();
            try {
                Expression expression = Parser.parse(content);
                exResult = expression.evaluate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return exResult;
    }

    public void compareResult(double caculate, String speech){

        String number = ExpressionUtils.convertNumber(speech);

        double result = ExpressionUtils.getDoubleFromString(number);
        String voice ;
        if(Math.abs(caculate-result) < diff){
            voice = rightSpeech;
        }else {
            voice = wrongSpeech;
        }
        startSpeechVoice(voice);
    }

    private SynthesizerListener mSynListener = new SynthesizerListener(){
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {}
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {}
        //暂停播放
        public void onSpeakPaused() {}
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
    };
}
