package com.xiaomi.client.view;

/**
 * @author yeshuxin on 16-10-24.
 */

public interface IHomeView {

    void OnSpeechResult(String result,boolean isCalculate);
    void onCaculateResult(double result);
}
