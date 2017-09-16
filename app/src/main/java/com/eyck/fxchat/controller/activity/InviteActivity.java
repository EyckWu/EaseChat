package com.eyck.fxchat.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.eyck.fxchat.R;
import com.eyck.fxchat.controller.adapter.InviteAdapter;
import com.eyck.fxchat.model.Model;
import com.eyck.fxchat.model.bean.InvitationInfo;
import com.eyck.fxchat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class InviteActivity extends Activity {

    private ListView lv_invite;
    private InviteAdapter inviteAdapter;
    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(final InvitationInfo invationInfo) {
            Model.getInstance().getGloalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //服务器
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());
                        //本地
                        Model.getInstance().getDbManager().getInviteDao().updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT,invationInfo.getUser().getHxid());
                        //内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请成功", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                        e.printStackTrace();
                    }


                }
            });
        }

        @Override
        public void onReject(final InvitationInfo invationInfo) {
            Model.getInstance().getGloalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //服务器
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());
                        //本地
                        Model.getInstance().getDbManager().getInviteDao().removeInvitation(invationInfo.getUser().getHxid());
                        //内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请成功", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请失败", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                        e.printStackTrace();
                    }


                }
            });
        }

        @Override
        public void onInviteAccept(InvitationInfo invationInfo) {
            Model.getInstance().getGloalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
//                    EMClient.getInstance().groupManager().acceptInvitation()
                }
            });
        }

        @Override
        public void onInviteReject(InvitationInfo invationInfo) {

        }

        @Override
        public void onApplicationAccept(InvitationInfo invationInfo) {

        }

        @Override
        public void onApplicationReject(InvitationInfo invationInfo) {

        }
    };
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();

        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(receiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(receiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void initData() {
        // 初始化listview
        inviteAdapter = new InviteAdapter(this, mOnInviteListener);

        lv_invite.setAdapter(inviteAdapter);

        // 刷新方法
        refresh();
    }

    private void refresh() {
        // 获取数据库中的所有邀请信息
        List<InvitationInfo> invitations = Model.getInstance().getDbManager().getInviteDao().getInvitations();

        // 刷新适配器
        inviteAdapter.refresh(invitations);
    }

    private void initView() {
        lv_invite = (ListView)findViewById(R.id.lv_invite);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(receiver);
    }
}
