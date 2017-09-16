package com.eyck.fxchat.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eyck.fxchat.R;
import com.eyck.fxchat.controller.activity.AddContactActivity;
import com.eyck.fxchat.controller.activity.ChatActivity;
import com.eyck.fxchat.controller.activity.GroupListActivity;
import com.eyck.fxchat.controller.activity.InviteActivity;
import com.eyck.fxchat.model.Model;
import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.utils.Constant;
import com.eyck.fxchat.utils.SPUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eyck on 2017/8/9.
 */

public class ContactListFragment extends EaseContactListFragment {
    private LinearLayout ll_contact_invite;
    private LinearLayout ll_contact_group;
    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private String mHxid;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 红点处理
            iv_contact_red.setVisibility(View.VISIBLE);
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, false);
//            getContactFromHxServer();
        }
    };
    private BroadcastReceiver contactReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshContact();
        }
    };
    private BroadcastReceiver groupChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 红点处理
            iv_contact_red.setVisibility(View.VISIBLE);
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, false);
        }
    };

    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.drawable.em_add);

        View headerView = View.inflate(getActivity(),R.layout.header_fragment_contact, null);

        listView.addHeaderView(headerView);

        ll_contact_invite = (LinearLayout) headerView.findViewById(R.id.ll_contact_invite);
        ll_contact_group = (LinearLayout) headerView.findViewById(R.id.ll_contact_group);
        iv_contact_red = (ImageView) headerView.findViewById(R.id.iv_contact_red);

        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);

                startActivity(intent);
            }
        });


        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                getActivity().startActivity(intent);
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(getActivity(), AddContactActivity.class);

                getActivity().startActivity(intent);
            }
        });
        iv_contact_red.setVisibility(SPUtils.getInstance().getBoolean(SPUtils.IS_NEW_INVITE,false)?View.VISIBLE:View.GONE);

        // 邀请信息条目点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 红点处理
                iv_contact_red.setVisibility(View.GONE);
                SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, false);

                // 跳转到邀请信息列表页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);

                startActivity(intent);
            }
        });

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(receiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(contactReceiver,new IntentFilter(Constant.CONTACT_CHANGE));
        mLBM.registerReceiver(groupChangedReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));


        // 从环信服务器获取所有的联系人信息
        getContactFromHxServer();

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();

        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.contact_delete) {
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteContact() {
        Model.getInstance().getGloalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    // 本地数据库的更新
                    Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(mHxid);

                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toast提示
                            Toast.makeText(getActivity(), "删除" + mHxid + "成功", Toast.LENGTH_SHORT).show();

                            // 刷新页面
                            refreshContact();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + mHxid + "失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGloalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    if(hxids != null && hxids.size()>=0) {
                        List<UserInfo> contacts = new ArrayList<UserInfo>();
                        for (String hxid:hxids){
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }
                        Model.getInstance().getDbManager().getContactDao().saveContacts(contacts,true);
                        if(getActivity() == null) {
                            return ;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新页面的方法
                                refreshContact();
                            }
                        });
                    }
                    
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshContact() {
        // 获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactDao().getContacts();
        // 校验
        if (contacts != null && contacts.size() >= 0) {

            // 设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();

            // 转换
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());

                contactsMap.put(contact.getHxid(), easeUser);
            }

            setContactsMap(contactsMap);

            // 刷新页面
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(receiver);
        mLBM.unregisterReceiver(contactReceiver);
    }
}
