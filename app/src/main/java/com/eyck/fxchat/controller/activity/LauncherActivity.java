package com.eyck.fxchat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.eyck.fxchat.R;
import com.eyck.fxchat.model.Model;
import com.eyck.fxchat.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

public class LauncherActivity extends Activity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 如果当前activity已经退出，那么我就不处理handler中的消息
            if(isFinishing()) {
                return;
            }

            // 判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    /**
     * 判断进入主界面还是登录页面
     */
    private void toMainOrLogin() {
        Model.getInstance().getGloalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if(EMClient.getInstance().isLoggedInBefore()){//登录过

                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());

                    if(account == null) {
                        Intent intent = new Intent(LauncherActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Model.getInstance().loginSuccess(account);
                        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else{
                    Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);

        // 发送2s钟的延时消息
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
