package com.eyck.fxchat.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eyck.fxchat.model.dao.ContactTab;
import com.eyck.fxchat.model.dao.InviteTab;

/**
 * Created by Eyck on 2017/8/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactTab.CREATE_TAB);
        // 创建邀请信息的表
        db.execSQL(InviteTab.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
