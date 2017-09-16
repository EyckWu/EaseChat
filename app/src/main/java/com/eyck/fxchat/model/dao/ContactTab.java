package com.eyck.fxchat.model.dao;

/**
 * Created by Eyck on 2017/8/10.
 */

public class ContactTab {
//    public static final String DB_NAME = "db_account";
    public static final String TAB_NAME = "tab_contact";
    public static final String COL_NAME = "name";
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";

    public static final String COL_IS_CONTACT = "is_contact";

    public static final String CREATE_TAB = "create table " +
            TAB_NAME + " (" +
            COL_HXID + " text primary key," +
            COL_NAME + " text," +
            COL_NICK + " text," +
            COL_PHOTO + " text," +
            COL_IS_CONTACT + " integer);";
}
