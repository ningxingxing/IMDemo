package com.example.apple.imdemo.model;

import android.content.Context;
import android.util.Log;

import com.example.apple.imdemo.model.bean.UserInfo;
import com.example.apple.imdemo.model.dao.UserAccountDao;
import com.example.apple.imdemo.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by apple on 17/1/5.
 *
 * 数据模型层全局类
 */

public class Model {

    private UserAccountDao userAccountDao;
    private Context mContext;
    private ExecutorService executorService = Executors.newCachedThreadPool();//线程池

    private DBManager dbManager;

    private final String TAG = "Model";

    private static Model model = new Model();

    private Model() {

    }

    //获取单例对象
    public static Model getInstance(){
        return model;
    }

    //初始化方法
    public void init(Context context){
        mContext = context;

        //创建用户账号数据库的操作类对象

        userAccountDao =  new UserAccountDao(context);

        //开启全局监听
        EventListener eventListener = new EventListener(mContext);

        Log.e(TAG,"EventListener执行");
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executorService;
    }

    public void loginSuccess(UserInfo account){

        //校验
        if (account == null){
            return;
        }

        if (dbManager !=null){
            dbManager.close();
        }


        dbManager =new DBManager(mContext,account.getName());

    }

    public DBManager getDbManager(){
        return dbManager;
    }

    //获取用户账号数据库的操作类对象
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }
}
