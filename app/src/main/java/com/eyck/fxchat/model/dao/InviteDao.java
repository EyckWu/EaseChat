package com.eyck.fxchat.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eyck.fxchat.model.bean.GroupInfo;
import com.eyck.fxchat.model.bean.InvitationInfo;
import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyck on 2017/8/10.
 */

public class InviteDao {
    private DBHelper mHelper;

    public InviteDao(DBHelper helper) {
        mHelper = helper;
    }

    // 添加邀请
    public void addInvitation(InvitationInfo invitationInfo) {
        // 获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTab.COL_REASON,invitationInfo.getReason());
        values.put(InviteTab.COL_STATUS,invitationInfo.getStatus().ordinal());

        UserInfo user = invitationInfo.getUser();
        if(user != null) {
            values.put(InviteTab.COL_USER_HXID,user.getHxid());
            values.put(InviteTab.COL_USER_NAME,user.getName());
        }else {
            values.put(InviteTab.COL_GROUP_HXID,invitationInfo.getGroup().getGroupId());
            values.put(InviteTab.COL_GROUP_NAME,invitationInfo.getGroup().getGroupName());
            values.put(InviteTab.COL_USER_HXID,invitationInfo.getGroup().getInvatePerson());
        }

        db.replace(InviteTab.TAB_NAME,null,values);

    }

    // 获取所有邀请信息
    public List<InvitationInfo> getInvitations() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from "+ InviteTab.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        List<InvitationInfo> invitationInfos = new ArrayList<>();
        while (cursor.moveToNext()){
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTab.COL_REASON)));
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTab.COL_STATUS))));

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_HXID));

            if (groupId == null) {// 联系人的邀请信息

                UserInfo userInfo = new UserInfo();

                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_NAME)));

                invitationInfo.setUser(userInfo);
            } else {// 群组的邀请信息
                GroupInfo groupInfo = new GroupInfo();

                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_HXID)));

                invitationInfo.setGroup(groupInfo);
            }

            invitationInfos.add(invitationInfo);
        }
        return invitationInfos;

    }

    // 将int类型状态转换为邀请的状态
    private InvitationInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    // 删除邀请
    public void removeInvitation(String hxId) {
        if (hxId == null) {
            return;
        }

        SQLiteDatabase db = mHelper.getReadableDatabase();

        db.delete(InviteTab.TAB_NAME,InviteTab.COL_USER_HXID + " =?",new String[]{hxId});

    }

    // 更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus invitationStatus, String hxId) {
        if (hxId == null) {
            return;
        }

        SQLiteDatabase db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTab.COL_STATUS, invitationStatus.ordinal());

        db.update(InviteTab.TAB_NAME,values,InviteTab.COL_USER_HXID + " =?",new String[]{hxId});

    }

}
