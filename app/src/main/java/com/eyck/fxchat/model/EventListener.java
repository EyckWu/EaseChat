package com.eyck.fxchat.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.eyck.fxchat.model.bean.GroupInfo;
import com.eyck.fxchat.model.bean.InvitationInfo;
import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.utils.Constant;
import com.eyck.fxchat.utils.MyLog;
import com.eyck.fxchat.utils.SPUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

/**
 * Created by Eyck on 2017/8/10.
 */

public class EventListener {

    private Context mContext;
    private final LocalBroadcastManager mLBM;


    public EventListener(Context context){

        mContext = context;

        //广播管理者
        mLBM = LocalBroadcastManager.getInstance(mContext);

        // 注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContectListener);
        // 注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);

    }

    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            MyLog.d("onInvitationReceived()");
            refreshData(groupName, groupId, inviter, reason, InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
        }

        //收到 群申请通知
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            MyLog.d("onRequestToJoinReceived()");
            refreshData(groupName, groupId, applicant, reason, InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
        }

        //收到 群申请被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            MyLog.d("onRequestToJoinAccepted()");
            refreshData(groupName, groupId, accepter, null, InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
        }

        //收到 群申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            MyLog.d("onRequestToJoinDeclined()");
            refreshData(groupName, groupId, decliner, reason, InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
        }

        //收到 群邀请被接受
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            MyLog.d("onInvitationAccepted()");
            refreshData(groupId, groupId, inviter, reason, InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            MyLog.d("onInvitationDeclined()");
            refreshData(groupId, groupId, inviter, reason, InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
        }

        @Override
        public void onUserRemoved(String s, String s1) {

        }

        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            MyLog.d("onAutoAcceptInvitationFromGroup()");
            refreshData(groupId, groupId, inviter, inviteMessage, InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };

    private void refreshData(String groupName, String groupId, String inviter, String reason, InvitationInfo.InvitationStatus newGroupInvite) {
        // 更新数据
        InvitationInfo invitationInfo = new InvitationInfo();
        invitationInfo.setReason(reason);
        invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
        invitationInfo.setStatus(newGroupInvite);

        Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);

        // 红点处理
        SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

        // 发送广播
        mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
    }

    private final EMContactListener emContectListener = new EMContactListener() {
        @Override
        public void onContactAdded(String hxid) {
            //更新数据库
            Model.getInstance().getDbManager().getContactDao().saveContact(new UserInfo(hxid),true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGE));

        }

        @Override
        public void onContactDeleted(String hxid) {
            //更新数据库
            Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteDao().removeInvitation(hxid);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGE));
        }

        @Override
        public void onContactInvited(String hxid, String reason) {
            //更新数据库
            InvitationInfo invitation = new InvitationInfo();
            invitation.setUser(new UserInfo(hxid));
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);// 新邀请
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitation);

            // 红点的处理
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        @Override
        public void onFriendRequestAccepted(String hxid) {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);// 别人同意了你的邀请

            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);

            // 红点的处理
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        @Override
        public void onFriendRequestDeclined(String hxid) {
            // 红点的处理
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

}
