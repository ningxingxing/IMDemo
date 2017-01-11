package com.example.apple.imdemo.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.activity.LoginActivity;
import com.example.apple.imdemo.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 17/1/5.
 *
 * 设置界面
 */

public class SettingFragment extends Fragment {

    @BindView(R.id.settings)
    TextView settings;
    @BindView(R.id.settings_exit)
    Button settingsExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }


    private void initData() {
        //在button上显示当前用户名称
        settingsExit.setText("退出登录(" + EMClient.getInstance().getCurrentUser()+")");
        settingsExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogout();
            }
        });
    }

    /**
     * 退出登陆
     */
    private void setLogout(){
        //  EMClient.getInstance().logout(true);//同步方法
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //异步方法
                // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
                EMClient.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {

                        //关闭DBHelper
                        Model.getInstance().getDbManager().close();

                        Log.e("lzan13", "logout success");
                        // 调用退出成功，结束app,更新ui显示
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //更新ui
                                Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();//结束当前界面
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("lzan13", "logout error " + i + " - " + s);

                        Toast.makeText(getActivity(),"logout error " + i + " - " + s,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });

    }
}
