package com.eyck.fxchat.controller.fragment;

import android.content.Intent;

import com.eyck.fxchat.controller.activity.ChatActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

/**
 * Created by Eyck on 2017/8/9.
 */

public class ChatFragment extends EaseConversationListFragment {
    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            // 设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);

            // 刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    @Override
    protected void initView() {
        super.initView();
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                getActivity().startActivity(intent);
            }
        });
        conversationList.clear();
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }
}
