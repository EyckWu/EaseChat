package com.eyck.fxchat.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.model.db.UserCountDB;

/**
 * Created by Eyck on 2017/8/9.
 */

public class UserAccountDao {

    private final UserCountDB mHelper;

    public UserAccountDao(Context context){
        mHelper = new UserCountDB(context);
    }

    public void addAccount(UserInfo user){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserAccountTab.COL_HXID,user.getHxid());
        values.put(UserAccountTab.COL_NAME,user.getName());
        values.put(UserAccountTab.COL_NICK,user.getNick());
        values.put(UserAccountTab.COL_PHOTO,user.getPhoto());

        db.replace(UserAccountTab.TAB_NAME,null,values);
    }

    public UserInfo getAccountByHxId(String hxId){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from " + UserAccountTab.TAB_NAME + " where " + UserAccountTab.COL_HXID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo = null;
        if(cursor.moveToNext()) {
            userInfo = new UserInfo();
            // 封装对象
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTab.COL_PHOTO)));
        }
        cursor.close();
        return userInfo;
    }

}
