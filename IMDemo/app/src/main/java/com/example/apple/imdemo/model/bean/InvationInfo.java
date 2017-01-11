package com.example.apple.imdemo.model.bean;


/**
 * Created by apple on 17/1/6.
 * 邀请信息的bean
 */

public class InvationInfo {
    private UserInfo userInfo;  //联系人的
    private GroupInfo groupInfo; //群的

    private String reason;//邀请原因

    private InvitationStatus status;//邀请状态

    public enum InvitationStatus{
        // 联系人邀请信息状态
        NEW_INVITE,// 新邀请
        INVITE_ACCEPT,//接受邀请
        INVITE_ACCEPT_BY_PEER,// 邀请被接受

        // --以下是群组邀请信息状态--

        //收到邀请去加入群
        NEW_GROUP_INVITE,

        //收到申请群加入
        NEW_GROUP_APPLICATION,

        //群邀请已经被对方接受
        GROUP_INVITE_ACCEPTED,

        //群申请已经被批准
        GROUP_APPLICATION_ACCEPTED,

        //接受了群邀请
        GROUP_ACCEPT_INVITE,

        //批准的群加入申请
        GROUP_ACCEPT_APPLICATION,

        //拒绝了群邀请
        GROUP_REJECT_INVITE,

        //拒绝了群申请加入
        GROUP_REJECT_APPLICATION,

        //群邀请被对方拒绝
        GROUP_INVITE_DECLINED,

        //群申请被拒绝
        GROUP_APPLICATION_DECLINED
    }

    public InvationInfo() {
    }

    public InvationInfo(UserInfo userInfo, GroupInfo groupInfo, String reason, InvitationStatus status) {
        this.userInfo = userInfo;
        this.groupInfo = groupInfo;
        this.reason = reason;
        this.status = status;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InvationInfo{" +
                "userInfo=" + userInfo +
                ", groupInfo=" + groupInfo +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}
