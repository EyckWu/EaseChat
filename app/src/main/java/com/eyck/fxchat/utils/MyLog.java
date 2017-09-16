package com.eyck.fxchat.utils;


import android.util.Log;

/**
 * Created by Eyck on 2017/8/8.
 */

public class MyLog {
    private static final String TAG = "EYCK";
    private static final boolean DEBUG = true;
    public static void d(String msg){
        if(DEBUG) {
            Log.d(TAG,msg);
        }
    }
    public static void e(String msg){
        if(DEBUG) {
            Log.e(TAG,msg);
        }
    }
}
