package com.example.apple.imdemo.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.apple.imdemo.model.dao.UserAccountTable;

/**
 * Created by apple on 17/1/5.
 */

public class UserAccountDB  extends SQLiteOpenHelper{



    public UserAccountDB(Context context) {
        super(context, " account.db", null, 1);
    }

    //创建调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    //更新调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
