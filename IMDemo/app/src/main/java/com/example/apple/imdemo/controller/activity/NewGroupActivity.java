package com.example.apple.imdemo.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//创建新群
public class NewGroupActivity extends AppCompatActivity {

    @BindView(R.id.new_group_name)
    EditText newGroupName;
    @BindView(R.id.new_group_desc)
    EditText newGroupDesc;
    @BindView(R.id.new_group_public)
    CheckBox newGroupPublic;
    @BindView(R.id.new_group_invite)
    CheckBox newGroupInvite;
    @BindView(R.id.new_group_create)
    Button newGroupCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {//隐藏标题栏
            getSupportActionBar().hide();
        }

        initData();

    }

    private void initData() {

    }


    @OnClick({ R.id.new_group_create})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.new_group_create://创建按钮的点击事件
                // 跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);

                startActivityForResult(intent, 1);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 成功获取到联系人
        if (resultCode == RESULT_OK) {
            // 创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    /**
     *创建群
     */
    private void createGroup(final String[] memberses) {
        //群名称
        final String groupName = newGroupName.getText().toString();
        //群描述
        final String groupDesc = newGroupDesc.getText().toString();

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                //去环信服务器创建群
                //参数一：群名称：参数二：群描述：参数三：群成员：参数四：原因：参数五：参数设置
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();

                options.maxUsers = 200;//群最多容纳多撒后人
                EMGroupManager.EMGroupStyle groupStyle = null;

                if (newGroupPublic.isChecked()){//公开

                    if (newGroupInvite.isChecked()){
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;//开放群邀请
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;//需要邀请
                    }

                }else {
                    if (newGroupInvite.isChecked()){//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;//群成员也可以邀请
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;//只有群主
                    }

                }

                options.style = groupStyle;//创建群类型
               try {

                   EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,memberses,"申请加入群",options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(),"创建群成功",Toast.LENGTH_SHORT).show();
                            //结束当前页面
                            finish();
                        }
                    });

               }catch (Exception e){
                   e.printStackTrace();
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(getApplication(),"创建群失败",Toast.LENGTH_SHORT).show();
                       }
                   });
               }

            }
        });
    }
}
