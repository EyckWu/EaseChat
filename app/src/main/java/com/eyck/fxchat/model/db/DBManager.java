package com.eyck.fxchat.model.db;

import android.content.Context;

import com.eyck.fxchat.model.dao.ContactDao;
import com.eyck.fxchat.model.dao.InviteDao;

/**
 * Created by Eyck on 2017/8/10.
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactDao contactDao;
    private final InviteDao inviteDao;

    public DBManager(Context context, String name){
        dbHelper = new DBHelper(context, name);
        contactDao = new ContactDao(dbHelper);
        inviteDao = new InviteDao(dbHelper);
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public InviteDao getInviteDao() {
        return inviteDao;
    }

    public void close(){
        dbHelper.close();
    }
}
