package com.eyck.fxchat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eyck.fxchat.R;
import com.eyck.fxchat.controller.adapter.GroupListAdapter;
import com.eyck.fxchat.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class GroupListActivity extends Activity {
    private ListView lv_grouplist;
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initView();

        initData();

        initListener();
    }

    private void initListener() {
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    return;
                }
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,EMClient.getInstance().groupManager().getAllGroups().get(position-1).getGroupId());
                startActivity(intent);
            }
        });

        // 跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);

                startActivity(intent);
            }
        });

    }

    private void initData() {
        // 初始化listview
        groupListAdapter = new GroupListAdapter(this);

        lv_grouplist.setAdapter(groupListAdapter);

        // 从环信服务器获取所有群的信息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGloalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EMGroup> groupsFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });



                } catch (HyphenateException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void initView() {
        // 获取listview对象
        lv_grouplist = (ListView)findViewById(R.id.lv_grouplist);

        // 添加头布局
        View headerView = View.inflate(this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headerView);

        ll_grouplist = (LinearLayout) headerView.findViewById(R.id.ll_grouplist);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 刷新页面
        refresh();
    }

    private void refresh() {
        List<EMGroup> emGroups = EMClient.getInstance().groupManager().getAllGroups();

        groupListAdapter.refresh(emGroups);
    }
}
