package com.example.apple.imdemo;

import android.app.Application;
import android.content.Context;

import com.example.apple.imdemo.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by apple on 17/1/5.
 */

public class IMApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化EaseUi
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);

        EaseUI.getInstance().init(this,options);


        //初始化数据模型层
        Model.getInstance().init(this);

        //初始化全局上下文
        mContext = this;
    }

    //全局上下文对象
    public static Context getGlobalApplication(){
        return mContext;
    }
}
