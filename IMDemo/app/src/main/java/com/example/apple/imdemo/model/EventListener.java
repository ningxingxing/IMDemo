package com.example.apple.imdemo.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.apple.imdemo.model.bean.GroupInfo;
import com.example.apple.imdemo.model.bean.InvationInfo;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.example.apple.imdemo.utils.Constant;
import com.example.apple.imdemo.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by apple on 17/1/7.
 *
 * //全局事件监听类
 */

public class EventListener {


    private Context mContext;
    private LocalBroadcastManager localBroadcastManager;

    private final String TAG = "EventListener";

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播的管理者对象
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangeListener);

    }

    /**
     * 注册一个群信息变化的监听
     */
    private final EMGroupChangeListener eMGroupChangeListener = new EMGroupChangeListener() {
        //收到群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String invite, String reason) {

            Log.e(TAG,"onInvitationReceived="+groupId + "groupName="+groupName+"：invite="+invite);
            //数据更新
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(reason);
            initationInfo.setGroupInfo(new GroupInfo(groupName,groupId,invite));
            initationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String application, String reason) {
            Log.e(TAG,"onApplicationReceived="+groupId + "groupName="+groupName+"：invite="+application);
            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(reason);
            initationInfo.setGroupInfo(new GroupInfo(groupName,groupId,application));
            initationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);//申请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accept) {
            Log.e(TAG,"onApplicationAccept="+groupId + "groupName="+groupName+"：invite="+accept);

            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setGroupInfo(new GroupInfo(groupName,groupId,accept));
            initationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);//申请被接受

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);


            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            Log.e(TAG,"onInvitationReceived="+groupId + "groupName="+groupName+"：invite="+decliner);

            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(reason);
            initationInfo.setGroupInfo(new GroupInfo(groupName,groupId,decliner));
            initationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);//申请被接受

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String invite, String reason) {
            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(reason);
            initationInfo.setGroupInfo(new GroupInfo(groupId,groupId,invite));
            initationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);//申请被接受

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invite, String reason) {
            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(reason);
            initationInfo.setGroupInfo(new GroupInfo(groupId,groupId,invite));
            initationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);//申请被接受

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String invite, String inviteMessage) {
            //更新本地数据库
            InvationInfo initationInfo = new InvationInfo();
            initationInfo.setReason(inviteMessage);
            initationInfo.setGroupInfo(new GroupInfo(groupId,groupId,invite));
            initationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);//申请被接受

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(initationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);//有新的邀请人信息

            //发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };



    /**
     * 注册一个联系人变化的监听
     */
    private final EMContactListener emContactListener = new EMContactListener() {

        //联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {
            //数据库更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid),true);

            //发送联系人变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

            Log.e(TAG,"onContactAdded="+hxid);
        }

        //联系人删除后的方法
        @Override
        public void onContactDeleted(String hxid) {

            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);

            //发送联系人变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

            Log.e(TAG,"onContactDeleted="+hxid);
        }

        //接收到联系人的新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setUserInfo(new UserInfo(hxid));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
            Log.e(TAG,"onContactInvited="+hxid + "reason="+reason);
        }

        //联系人同意的邀请
        @Override
        public void onContactAgreed(String hxid) {

            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setUserInfo(new UserInfo(hxid));
            invationInfo.setStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);//别人同意了你的邀请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

            Log.e(TAG,"onContactAgreed="+hxid);
        }

        //别人拒绝了你的邀请
        @Override
        public void onContactRefused(String hxid) {
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));//邀请信息变化

            Log.e(TAG,"onContactRefused="+hxid);
        }
    };
}
