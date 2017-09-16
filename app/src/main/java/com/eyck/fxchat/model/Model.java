package com.eyck.fxchat.model;

import android.content.Context;

import com.eyck.fxchat.model.bean.UserInfo;
import com.eyck.fxchat.model.dao.UserAccountDao;
import com.eyck.fxchat.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Eyck on 2017/8/9.
 */
//数据模型层全局类
public class Model {
    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;
    private EventListener eventListener;
    //    private DBManager dbManager;

    /**
     * 私有化构造
     */
    private Model(){}

    /**
     * 获取单例对象
     * @return
     */
    public static Model getInstance(){
        return ModelHolder.model;
    }

    public void loginSuccess(UserInfo userInfo) {
        if(userInfo == null) {
            return;
        }
        if(dbManager != null) {
            dbManager.close();
        }

        dbManager = new DBManager(mContext, userInfo.getName());
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public UserAccountDao getUserAccountDao() {
        return userAccountDao;
    }

    private static class ModelHolder{
        private static final Model model = new Model();
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        mContext = context;
        userAccountDao = new UserAccountDao(mContext);
        eventListener = new EventListener(mContext);
    }

    public ExecutorService getGloalThreadPool(){
        return executors;
    }


}
