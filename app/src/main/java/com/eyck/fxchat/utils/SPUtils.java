package com.eyck.fxchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.eyck.fxchat.IMApplication;

/**
 * Created by Eyck on 2017/8/10.
 */

public class SPUtils {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SharedPreferences mSP;
    //    private static final SPUtils = SPUtisHolder.

    private SPUtils(){}

    public static SPUtils getInstance(){

        if(mSP == null) {
            mSP = IMApplication.getGlobalApplication().getSharedPreferences("fx_chat", Context.MODE_PRIVATE);
        }

        return SPUtisHolder.instance;
    }
    private static class SPUtisHolder{
        private static final SPUtils instance = new SPUtils();
    }

    public void save(String key,Object value){
        if(value instanceof String) {
            mSP.edit().putString(key, (String) value).commit();
        }else if(value instanceof Boolean) {
            mSP.edit().putBoolean(key, (Boolean) value).commit();
        }else if(value instanceof Integer) {
            mSP.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 获取数据的方法
    public String getString(String key, String defValue) {
        return mSP.getString(key, defValue);
    }

    // 获取boolean数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSP.getBoolean(key, defValue);
    }

    // 获取int类型数据
    public int getInt(String key, int defValue) {
        return mSP.getInt(key, defValue);
    }

}
