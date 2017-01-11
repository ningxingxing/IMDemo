package com.example.apple.imdemo.model.bean;

/**
 * Created by apple on 2017/1/9.
 * 用于存放创建群时选择人的信息
 */

public class PickContactInfo {
    private UserInfo user;      // 联系人
    private boolean isChecked;  // 是否被选择的标记

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public PickContactInfo() {
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
