package com.example.apple.imdemo.controller.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.activity.AddContactActivity;
import com.example.apple.imdemo.controller.activity.ChatActivity;
import com.example.apple.imdemo.controller.activity.GroupListActivity;
import com.example.apple.imdemo.controller.activity.InviteActivity;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.example.apple.imdemo.utils.Constant;
import com.example.apple.imdemo.utils.SpUtils;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 17/1/5.
 * 联系人
 */

public class ContactFragment extends EaseContactListFragment {

    private ImageView contactRed;//红点
    private LinearLayout contactInvite;//条目
    private LinearLayout contactGroup;
    private String mHxid;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver ConstantInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            contactRed.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };

    //联系人变化广播
    private BroadcastReceiver ConstantChangeReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refreshContact();
        }
    };

    //群信息变化广播
    private BroadcastReceiver GroupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //显示红点
            contactRed.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };


    @Override
    protected void initView() {
        super.initView();

        //布局显示加号
        titleBar.setRightImageResource(R.mipmap.icon_addpic_unfocused);

        //添加头布局
        View view = View.inflate(getActivity(), R.layout.headher_fragment_contact, null);
        listView.addHeaderView(view);

        contactRed = (ImageView)view.findViewById(R.id.contact_red);
        contactInvite = (LinearLayout)view.findViewById(R.id.contact_invite);
        contactGroup = (LinearLayout)view.findViewById(R.id.contact_group);//群

        //设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                if (user == null){
                    return;
                }

                Intent intent =new Intent(getActivity(),ChatActivity.class);
                //传递参数,环信id
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                startActivity(intent);
            }
        });

        contactGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GroupListActivity.class);

                startActivity(intent);
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();

        //添加按钮的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加好友
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });


        //初始化红点的显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        contactRed.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //邀请信息条目点击事件
        contactInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //红点处理
                contactRed.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);

                //跳转到邀请信息列表页面
                Intent intent = new Intent(getActivity(),InviteActivity.class);
                startActivity(intent);
            }
        });


        //注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        //联系人邀请更新广播
        localBroadcastManager.registerReceiver(ConstantInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));

        //联系人变化的广播，更新联系人变化界面
        localBroadcastManager.registerReceiver(ConstantChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));

        //群信息变化广播
        localBroadcastManager.registerReceiver(GroupChangeReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));


        //从环信服务器获取所有联系人信息
        getContactFromHxServer();

        //绑定listview和contextmenu
        registerForContextMenu(listView);

    }

    /**
     * 长按删除好友
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 获取环信id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

        //强转类型
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);

        mHxid = easeUser.getUsername();

        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.contact_delete){
            //删除选中的联系人操作
            deleteContact();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * 删除选中的联系人操作
     */
    private void deleteContact() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //从环信服务器中删除联系人
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    //更新本地数据库
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    if (getActivity() == null){
                        return;
                    }
                    //刷新页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"删除"+mHxid+"成功",Toast.LENGTH_SHORT).show();

                            //刷新页面
                            refreshContact();
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() == null){
                        return;
                    }
                    //刷新页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"删除"+mHxid+"失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    /**
     * 从环信服务器获取所有联系人信息
     */
    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取所有的好友的环信的id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    //校验
                    if (hxids !=null && hxids.size() >=0){

                        List<UserInfo> contacts = new ArrayList<UserInfo>();
                        //转换
                    for (String hxid : hxids){
                        UserInfo userInfo = new UserInfo(hxid);
                        contacts.add(userInfo);
                    }

                    //保存好友信息到本地数据库
                    Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts,true);

                    if (getActivity() == null){//有可能为空
                        return;
                    }
                    //刷新页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新页面方法
                            refreshContact();
                        }
                    });
                }


                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 刷新联系人
     */
    private void refreshContact() {

        // 获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        // 校验
        if (contacts != null && contacts.size() >= 0) {

            // 设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();

            // 转换成EaseUser数据类型
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());

                contactsMap.put(contact.getHxid(), easeUser);
            }

            setContactsMap(contactsMap);

            // 刷新页面
            refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁广播
        localBroadcastManager.unregisterReceiver(ConstantInviteChangeReceiver);
        localBroadcastManager.unregisterReceiver(ConstantChangeReceiver);
        localBroadcastManager.unregisterReceiver(GroupChangeReceiver);
    }
}
