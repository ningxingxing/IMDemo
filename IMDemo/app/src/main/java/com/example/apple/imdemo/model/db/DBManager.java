package com.example.apple.imdemo.model.db;

import android.content.Context;

import com.example.apple.imdemo.model.dao.ContactTableDao;
import com.example.apple.imdemo.model.dao.InviteTableDao;

/**
 * Created by apple on 17/1/7.
 *
 * 邀请人跟联系人的操作类的管理类
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;
    public DBManager(Context context,String name) {
        //创建数据库
        dbHelper = new DBHelper(context,name);
        //创建改数据库中两张表的操作类
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    //获取联系人表的操作类对象
    public ContactTableDao getContactTableDao(){
        return contactTableDao;
    }

    //获取邀请信息表的操作类对象
    public InviteTableDao getInviteTableDao(){
        return inviteTableDao;
    }

    //关闭数据库
    public void close() {
        dbHelper.close();
    }
}
