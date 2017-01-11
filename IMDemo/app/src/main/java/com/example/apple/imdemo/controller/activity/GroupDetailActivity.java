package com.example.apple.imdemo.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.adapter.GroupDetailAdapter;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.example.apple.imdemo.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.gv_group_detail)
    GridView gvGroupDetail;
    @BindView(R.id.bt_group_detail_out)
    Button btGroupDetailOut;

    private List<UserInfo> mUsers;
    private GroupDetailAdapter groupDetailAdapter;

    private EMGroup mGroup;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override

        public void onAddMembers() {
            //跳转到选择联系人页面
            Intent intent = new Intent(getApplication(), PickContactActivity.class);

            //传递群id
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
            startActivityForResult(intent, 2);

        }

        //删除群成员
        @Override
        public void onDeleteMenber(final UserInfo user) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //从环信服务器中删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxid());

                        //更新页面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            //获取返回的群成员信息
            final String[] memberses = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "发送邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {//隐藏标题栏
            getSupportActionBar().hide();
        }

        getData();

        initData();

        initListener();
    }

    private void initListener() {
        gvGroupDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        //判断当前是否删除模式，如果是删除模式
                        if (groupDetailAdapter.ismIsDeleteModel()){
                            //切换为非删除模式
                            groupDetailAdapter.setmIsDeleteModel(false);

                            //刷新页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }

                        break;
                }

                return false;
            }
        });
    }

    private void initData() {

        //初始化button显示
        initButtonDisplay();

        //初始化gridView
        initGridView();

        //从环信服务器获取所有群成员
        getMembersFromHxServer();
    }

    /**
     * 从环信服务器获取所有群成员
     */
    private void getMembersFromHxServer() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());

                    List<String> members = emGroup.getMembers();

                    if (members != null && members.size() >= 0) {
                        mUsers = new ArrayList<UserInfo>();

                        for (String member : members) {

                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.refresh(mUsers);
                        }
                    });


                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "获取群信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化gridView
     */
    private void initGridView() {

        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);
        gvGroupDetail.setAdapter(groupDetailAdapter);
    }

    /**
     * 初始化button显示
     */
    private void initButtonDisplay() {
        //判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {//群主

            btGroupDetailOut.setText("解散群");
            btGroupDetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送退群的广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "解散群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });


                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "解散群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        } else {//群成员
            btGroupDetailOut.setText("退群");

            btGroupDetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        //告诉环信服务器退群
                        EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                        //发送退群广播
                        exitGroupBroatCast();

                        //更新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "退群成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } catch (HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "退群失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });

        }

    }

    /**
     * 发送退群和解散群广播
     */
    private void exitGroupBroatCast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        localBroadcastManager.sendBroadcast(intent);
    }

    //获取传递过来的数据
    public void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);

        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }
}
