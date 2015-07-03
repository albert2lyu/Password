package com.daggerstudio.password.utils;

import android.util.Base64;

import java.util.Random;

/**
 * Created by alex on 15/5/25.
 */
public class PasswordUtil {
    private static String ALPHA_POOL = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
    private static String NUMBER_POOL = "12345678901234567890123456789012345678901234567890";
    private static String PUNCTUATION_POOL = "~!@#$%^&*().,/?><~!@#$%^&*().,/?><~!@#$%^&*().,/?><";


    public static String genertRandomPassword(int length, boolean hasNumber, boolean hasPunctuation){
        String pool = ALPHA_POOL;
        String result = "";
        Random random = new Random();
        int pl;
        if (hasNumber){
            pool += NUMBER_POOL;
        }
        if(hasPunctuation){
            pool += PUNCTUATION_POOL;
        }
        pl = pool.length();
        for (int i = 0;i<length;i++){
            result += pool.charAt(random.nextInt(pl));
        }
        return result;
    }


    private static String removeAllBlankNote(String input){
        return input.replaceAll("\n", "");
    }


    public static String sha1ThenBase64Password(String password){
        String internalResult = Base64.encodeToString(EncDecUtil.SHA1(password), Base64.DEFAULT);
        internalResult = removeAllBlankNote(internalResult);
        return internalResult;
    }
}
