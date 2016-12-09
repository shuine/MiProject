package com.xiaomi.client.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author yeshuxin on 16-10-24.
 */

public class ExpressionUtils {

    public static Set<Character> mNumberChar = new HashSet<>();
    public static Set<Character> mOpreatorChar = new HashSet<>();
    public static Map<Character,Integer> mLetterNumber = new HashMap<>();
    static {
        mNumberChar.add('0');mNumberChar.add('1');mNumberChar.add('2');mNumberChar.add('3');
        mNumberChar.add('4');mNumberChar.add('5');mNumberChar.add('6');mNumberChar.add('7');
        mNumberChar.add('8');mNumberChar.add('9');
        mOpreatorChar.add('.');mOpreatorChar.add('+');mOpreatorChar.add('-');mOpreatorChar.add('*');
        mOpreatorChar.add('/');
        mLetterNumber.put('零',0);mLetterNumber.put('一',1);mLetterNumber.put('二',2);mLetterNumber.put('三',3);
        mLetterNumber.put('四',4);mLetterNumber.put('五',5);mLetterNumber.put('六',6);mLetterNumber.put('七',7);
        mLetterNumber.put('八',8);mLetterNumber.put('九',9);mLetterNumber.put('十',10);
    }
    public static boolean isValidExpression(String content){
        if(TextUtils.isEmpty(content)){
            return false;
        }
        int length = content.length();
        for (int index = 0;index < length;index++){
            char letter = content.charAt(index);
            if (!mNumberChar.contains(letter) && !mOpreatorChar.contains(letter)){
                return false;
            }
        }
        return true;
    }

    public static String excludeInvalidChar(String content){
        if(TextUtils.isEmpty(content)){
            return null;
        }
        String text = content.replace("\\s*","").replace("/n","").replace('÷','/').replace('×','*');

        return text;
    }

    public static double getDoubleFromString(String content){
        double result = 0.0;
        StringBuilder subBuilder = new StringBuilder();
        char letter;
        if(!TextUtils.isEmpty(content)){
            int length = content.length();
            for (int index = 0;index < length;index++){
                letter = content.charAt(index);
                if(subBuilder.length() > 0 && letter ==  '.'){
                    subBuilder.append(letter);
                    continue;
                }
                if (mNumberChar.contains(letter)){
                    subBuilder.append(letter);
                }else if(subBuilder.length() > 0){
                    break;
                }
            }
        }
        if(!TextUtils.isEmpty(subBuilder.toString())) {
            result = Double.parseDouble(subBuilder.toString());
        }
        return result;
    }

    public static String convertNumber(String content){
        if(TextUtils.isEmpty(content)){
            return content;
        }
        StringBuilder result = new StringBuilder();
        boolean hasFront = false;
        char letter;
        int lenght = content.length();
        for (int index = 0; index < lenght;index++){
            letter = content.charAt(index);
            if(mLetterNumber.containsKey(letter)){
                if(letter == '十'){
                    char behind = index+1<lenght?content.charAt(index+1):'=';
                    boolean hasBehind = mLetterNumber.containsKey(behind);
                    if(hasFront && hasBehind){
                        //result.append("0");
                    }else if(hasFront && !hasBehind){
                        result.append("0");
                        //result.append(behind);
                    }else if(!hasFront && hasBehind){
                        result.append("1");
                    }
                }else {
                    result.append(mLetterNumber.get(letter));
                }
                hasFront = true;
            }else {
                result.append(letter);
                hasFront =false;
            }
        }
        return result.toString();
    }
}
