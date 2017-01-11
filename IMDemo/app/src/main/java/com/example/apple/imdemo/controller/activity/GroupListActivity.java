package com.example.apple.imdemo.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.adapter.GroupListAdapter;
import com.example.apple.imdemo.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupListActivity extends AppCompatActivity {

    @BindView(R.id.group_list)
    ListView groupList;
    private final String TAG = "GroupListActivity";

    private GroupListAdapter groupListAdapter;
    private LinearLayout llGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_group_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        ButterKnife.bind(this);

        initView();

        initData();

        initListener();

    }

    /**
     * 初始化监听
     */
    private void initListener() {

        //listview条目点击事件
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,"position="+position);

                if (position == 0){//第一条用来显示创建群组
                    return;
                }

                Intent intent = new Intent(getApplication(),ChatActivity.class);

                //传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);

                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position-1);
                //群id
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

                startActivity(intent);
            }
        });

        //跳转到新建群
        llGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this,NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化listview
        groupListAdapter = new GroupListAdapter(this);
        groupList.setAdapter(groupListAdapter);

        //从环信服务器获取所有群信息
        getGroupsFromserver();
    }

    /**
     * 从环信服务器获取所有群信息
     */
    private void getGroupsFromserver() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                     final List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                     //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(),"加载群信息成功",Toast.LENGTH_SHORT).show();

                           // groupListAdapter.refresh(mGroups);
                            //刷新
                            refresh();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"加载群信息失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    /**
     * 刷新
     */
    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        //添加头布局
        View headerView = View.inflate(this,R.layout.header_group_list,null);

        groupList.addHeaderView(headerView);

        llGroupList = (LinearLayout)headerView.findViewById(R.id.ll_groupList);

    }


    @Override
    protected void onResume() {
        super.onResume();

        //刷新页面
        refresh();
    }
}
