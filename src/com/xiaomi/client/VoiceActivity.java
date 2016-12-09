package com.xiaomi.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.xiaomi.client.presenter.SpeechPresenter;
import com.xiaomi.client.util.ExpressionUtils;
import com.xiaomi.client.util.ToastUtil;
import com.xiaomi.client.view.IHomeView;

/**
 * @author yeshuxin on 16-10-21.
 */

public class VoiceActivity extends Activity implements View.OnClickListener,IHomeView {

    private Button mBtnDialog;
    private Button mBtnStart;
    private TextView mTv;

    private SpeechPresenter mPresenter;
    private double mCalculateResult;
    private String mSpeechResult;
 //   private SpeechRecognizer mIat;
  //  private StringBuilder result = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        mPresenter = new SpeechPresenter(this,this);
        initView();
       // initSpeech();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView(){
        mBtnDialog = (Button) findViewById(R.id.voice_btn_dialog);
        mBtnStart = (Button) findViewById(R.id.voice_btn_start);
        mTv = (TextView) findViewById(R.id.voice_tv);
        mBtnDialog.setOnClickListener(this);
        mBtnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                 //   addText("Touch Down,start recognize");
                   // mIat.startListening(mRecoListener);
                    mPresenter.startSpeechListening();
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
               //     addText("Touch Up,stop recognize");
                 //   mIat.stopListening();
                    mPresenter.stopSpeechListening();
                    return true;
                }
                return false;
            }
        });

        // init calculate
        findViewById(R.id.btn_calculate_0).setOnClickListener(this);
        findViewById(R.id.btn_calculate_1).setOnClickListener(this);
        findViewById(R.id.btn_calculate_2).setOnClickListener(this);
        findViewById(R.id.btn_calculate_3).setOnClickListener(this);
        findViewById(R.id.btn_calculate_4).setOnClickListener(this);
        findViewById(R.id.btn_calculate_5).setOnClickListener(this);
        findViewById(R.id.btn_calculate_6).setOnClickListener(this);
        findViewById(R.id.btn_calculate_7).setOnClickListener(this);
        findViewById(R.id.btn_calculate_8).setOnClickListener(this);
        findViewById(R.id.btn_calculate_9).setOnClickListener(this);
        findViewById(R.id.btn_calculate_add).setOnClickListener(this);
        findViewById(R.id.btn_calculate_minus).setOnClickListener(this);
        findViewById(R.id.btn_calculate_multi).setOnClickListener(this);
        findViewById(R.id.btn_calculate_divide).setOnClickListener(this);
        findViewById(R.id.btn_calculate_cancel).setOnClickListener(this);
        findViewById(R.id.btn_calculate_equal).setOnClickListener(this);

        textMethod();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mBtnDialog.getId()){
            // startDialogSpeech();
            mPresenter.startDialogSpeech(false);
        }else if(v.getId() == R.id.btn_calculate_cancel){
            addText("",true);
        }else if(v.getId() == R.id.btn_calculate_equal){
            if(mPresenter != null){
                mPresenter.caculateExpression(mTv.getText().toString(),false);
            }
            mPresenter.startDialogSpeech(true);
        } else {
            Button btn= (Button)v;
            addText(btn.getText().toString(),false);
        }

    }

    private void textMethod(){
        /*addText("ab五.三="+ExpressionUtils.getDoubleFromString(ExpressionUtils.convertNumber("ab五.三")),false);
        addText("ab五十="+ExpressionUtils.getDoubleFromString(ExpressionUtils.convertNumber("ab五十")),false);
        addText("ab五十五="+ExpressionUtils.getDoubleFromString(ExpressionUtils.convertNumber("ab五十五")),false);
        addText("ab十="+ExpressionUtils.getDoubleFromString(ExpressionUtils.convertNumber("ab十")),false);
        addText("adf3.26="+ExpressionUtils.getDoubleFromString("adf3.26"),false);*/
    }

    private void addText(String str,boolean isEraser){
        if(isEraser) {
            mTv.setText(str);
        }else {
            String text = mTv.getText().toString();
            mTv.setText(text + str);
        }
    }

    @Override
    public void OnSpeechResult(String result,boolean isCalculate) {
       // addText(result,true);
        mSpeechResult = result;
        if(isCalculate) {
            ToastUtil.show(result);
            mPresenter.compareResult(mCalculateResult, result);
            addText("",true);
        }else {
            addText(result,true);
        }
    }

    @Override
    public void onCaculateResult(double result) {
        mCalculateResult = result;
      //  mPresenter.startDialogSpeech(true);
      //  addText(String.valueOf(result),false);
       // mPresenter.startSpeechVoice(String.valueOf(result));
    }

   /* private double parseInputContent(String content){
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
*/

   /* private void initSpeech(){
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat= SpeechRecognizer.createRecognizer(this, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

    }*/

   /* private RecognizerListener mRecoListener = new RecognizerListener() {
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
                addText(result.toString());
            }
        }

        @Override
        public void onError(SpeechError speechError) {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };*/



     /* private void startDialogSpeech(){
        result.delete(0,result.length());
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        RecognizerDialog iatDialog = new RecognizerDialog(this,null);
        //2.设置听写参数，同上节
        iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //3.设置回调接口
        iatDialog.setListener(dialogListener);
        //4.开始听写
        iatDialog.show();
    }*/

    /*private RecognizerDialogListener dialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            if(!b){
                result.append(JsonParser.parseIatResult(recognizerResult.getResultString()));
            }else {
                addText(result.toString());
            }
        }

        @Override
        public void onError(SpeechError speechError) {
        }
    };*/
}
