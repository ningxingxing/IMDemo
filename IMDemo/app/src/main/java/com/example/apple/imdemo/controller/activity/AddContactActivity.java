package com.example.apple.imdemo.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContactActivity extends AppCompatActivity {

    @BindView(R.id.add_find)
    TextView addFind;//查找
    @BindView(R.id.add_name)
    EditText addName;//名称输入
    @BindView(R.id.add_head)
    ImageView addHead;//头像
    @BindView(R.id.tv_add_name)
    TextView tvAddName;//姓名
    @BindView(R.id.add_add)
    Button addAdd;//添加
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;
    @BindView(R.id.activity_add_contact)
    LinearLayout activityAddContact;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

    }

    @OnClick({R.id.add_find, R.id.add_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_find:
                query();

                break;
            case R.id.add_add:

                 add();
                break;
        }
    }
    //查找按钮
    private void query() {
        //获取输入用户名称
        final String name = addName.getText().toString();

        //校验输入的名称
        if (TextUtils.isEmpty(name)){
            Toast.makeText(getApplication(),"输入的用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //去服务器判断当前用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //区服务器判断当前查找的用户是否存在
                userInfo = new UserInfo(name);

                //更新ui显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rlAdd.setVisibility(View.VISIBLE);
                        tvAddName.setText(userInfo.getName());
                    }
                });
            }
        });

    }

    //添加按钮
    private void add() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(),"添加好友");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"发送好友邀请成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"发送添加好友失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
