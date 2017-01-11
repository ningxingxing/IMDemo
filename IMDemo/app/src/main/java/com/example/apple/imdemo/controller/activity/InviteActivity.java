package com.example.apple.imdemo.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.adapter.InviteAdapter;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.InvationInfo;
import com.example.apple.imdemo.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteActivity extends AppCompatActivity {

    @BindView(R.id.invite)
    ListView invite;
    private  InviteAdapter inviteAdapter;
    private  LocalBroadcastManager localBroadcastManager;

    private final String TAG = "InviteActivity";

    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(final InvationInfo invationInfo) {
            //通知环信服务器，点击了接收按钮
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUserInfo().getHxid());
                        Log.e(TAG,"id="+invationInfo.getUserInfo().getHxid());
                        //数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT,invationInfo.getUserInfo().getHxid());

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接受了邀请",Toast.LENGTH_SHORT).show();

                                //刷新页面
                                reFresh();
                            }
                        });

                    } catch (HyphenateException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接收邀请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onReject(final InvationInfo invationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUserInfo().getHxid());
                        Log.e(TAG,"id="+invationInfo.getUserInfo().getHxid());
                        //本地数据是否需要变化
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invationInfo.getUserInfo().getHxid());

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝成功了",Toast.LENGTH_SHORT).show();

                                reFresh();
                            }
                        });


                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝失败了",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //接受邀请
        @Override
        public void onInviteAccept(final InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //告诉环信服务器接受了邀请
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroupInfo().getGroupId(),invationInfo.getGroupInfo().getInvatePerson());

                        //本地数据更新
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);


                        //内存数据的变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接受邀请",Toast.LENGTH_SHORT).show();

                                reFresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接受邀请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //拒绝邀请
        @Override
        public void onInviteReject(final InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //告诉环信服务器拒绝了邀请
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroupInfo().getGroupId(),invationInfo.getGroupInfo().getInvatePerson(),"拒绝邀请");

                        //更新本地数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝邀请",Toast.LENGTH_SHORT).show();

                                reFresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝邀请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //接受申请
        @Override
        public void onApplicationAccept(final InvationInfo invationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //告诉环信服务器接受了申请
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroupInfo().getGroupId(),invationInfo.getGroupInfo().getInvatePerson());

                        //更新本地数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接受申请",Toast.LENGTH_SHORT).show();

                                reFresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"接受申请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //拒绝申请
        @Override
        public void onApplicationReject(final InvationInfo invationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //告诉环信服务器拒绝了申请
                        EMClient.getInstance().groupManager().declineApplication(invationInfo.getGroupInfo().getGroupId(),invationInfo.getGroupInfo().getInvatePerson(),"拒绝申请");

                        //更新本地数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝申请",Toast.LENGTH_SHORT).show();

                                reFresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(),"拒绝申请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            reFresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化适配器
        inviteAdapter = new InviteAdapter(this,mOnInviteListener);
        invite.setAdapter(inviteAdapter);

        //刷新方法
        reFresh();

        //注册邀请信息变化的广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));

        //群信息变化广播
        localBroadcastManager.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));

    }

    /**
     * 刷新界面
     */
    private void reFresh() {

        //获取数据库中的所有邀请信息
        List<InvationInfo> invationInfos = Model.getInstance().getDbManager().getInviteTableDao().getInvitations();

        //刷新适配器
        inviteAdapter.refresh(invationInfos);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        localBroadcastManager.unregisterReceiver(InviteChangedReceiver);
    }
}
