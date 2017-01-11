package com.example.apple.imdemo.controller.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           // super.handleMessage(msg);
            //如果当前activity已经退出，那么不处理
            if (isFinishing()){
                Log.e(TAG,"登录过");
                return;
            }

            //判断进入主页面环是登录页面
            toMainOrLogin();
        }
    };

    //判断进入主页面环是登录页面
    private void toMainOrLogin(){

        //可减少内存优化
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断是否登录过
                if (EMClient.getInstance().isLoggedInBefore()){//登录过
                    //获取当前登录信息
                    Log.e(TAG,"登录过");
                    //获取本地数据库id
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());

                    if (account ==null){
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        startActivity(intent);
                    }else {

                        //登录成功后的方法
                        Model.getInstance().loginSuccess(account);

                        //跳转到主页面
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);
                    }

                }else {//没登录过
                    Log.e(TAG,"没登录过");
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    startActivity(intent);
                }

                //结束当前页面
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splish);

        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
