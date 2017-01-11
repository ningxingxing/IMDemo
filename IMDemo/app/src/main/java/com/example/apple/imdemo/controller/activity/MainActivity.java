package com.example.apple.imdemo.controller.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.fragment.ChatFragment;
import com.example.apple.imdemo.controller.fragment.ContactFragment;
import com.example.apple.imdemo.controller.fragment.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.rb_main_chat)
    RadioButton rbMainChat;
    @BindView(R.id.rb_main_contact)
    RadioButton rbMainContact;
    @BindView(R.id.rb_main_setting)
    RadioButton rbMainSetting;
    @BindView(R.id.rg_main)
    RadioGroup rgMain;

    private Fragment chatFragment;
    private Fragment contactFragment;
    private Fragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();

        initListener();
    }

    private void initData() {
         chatFragment = new ChatFragment();

         contactFragment = new ContactFragment();

         settingFragment = new SettingFragment();

    }

    private void initListener() {
        //RadioGroup监听
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Fragment fragment = null;

                switch (checkedId){
                    //会话
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;
                    //联系人
                    case R.id.rb_main_contact:
                        fragment = contactFragment;
                        break;
                    //设置
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }

                switchFragment(fragment);
            }


        });

        rgMain.check(R.id.rb_main_chat);

    }
    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.iv_main,fragment).commit();

    }

}
