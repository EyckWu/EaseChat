package com.eyck.fxchat.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eyck.fxchat.model.dao.UserAccountTab;

/**
 * Created by Eyck on 2017/8/9.
 */

public class UserCountDB extends SQLiteOpenHelper {
    public UserCountDB(Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTab.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
