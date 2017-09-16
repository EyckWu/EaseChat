package com.eyck.fxchat.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyck on 2017/8/10.
 */

public class ContactDao {

    private final DBHelper mHelper;

    public ContactDao(DBHelper helper) {
        mHelper = helper;
    }

    // 获取所有联系人
    public List<UserInfo> getContacts() {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from "+ ContactTab.TAB_NAME + " where " + ContactTab.COL_IS_CONTACT + " =1" ;
        Cursor cursor = db.rawQuery(sql, null);
        List<UserInfo> userInfos = new ArrayList<>();
        while (cursor.moveToNext()){

            UserInfo userInfo = new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTab.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTab.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTab.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTab.COL_PHOTO)));

            userInfos.add(userInfo);

        }

        //关闭资源
        cursor.close();

        //返回结果
        return userInfos;

    }

    // 通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId) {
        if (hxId == null) {
            return null;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from "+ ContactTab.TAB_NAME + " where " + ContactTab.COL_HXID + " =?" ;
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo = null;
        if (cursor.moveToNext()){

            userInfo = new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTab.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTab.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTab.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTab.COL_PHOTO)));


        }

        //关闭资源
        cursor.close();

        //返回结果
        return userInfo;
    }

    // 通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds) {
        if (hxIds == null) {
            return null;
        }
        List<UserInfo> userInfos = new ArrayList<>();
        for (String hxId : hxIds) {
            UserInfo userInfo = getContactByHx(hxId);
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    // 保存单个联系人
    public void saveContact(UserInfo user, boolean isMyContact) {
        if (user == null) {
            return;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactTab.COL_HXID,user.getHxid());
        values.put(ContactTab.COL_NAME,user.getName());
        values.put(ContactTab.COL_NICK,user.getNick());
        values.put(ContactTab.COL_PHOTO,user.getPhoto());
        values.put(ContactTab.COL_IS_CONTACT,isMyContact ? 1 : 0);

        db.replace(ContactTab.TAB_NAME,null,values);

    }


    // 保存联系人信息
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        if (contacts == null || contacts.size() <= 0) {
            return;
        }

        for (UserInfo contact : contacts) {
            saveContact(contact, isMyContact);
        }
    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId){
        if (hxId == null) {
            return;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.delete(ContactTab.TAB_NAME,ContactTab.COL_HXID+" =?",new String[]{hxId});
    }
}
