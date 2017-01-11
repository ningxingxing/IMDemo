package com.example.apple.imdemo.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.controller.adapter.PickContactAdapter;
import com.example.apple.imdemo.model.Model;
import com.example.apple.imdemo.model.bean.PickContactInfo;
import com.example.apple.imdemo.model.bean.UserInfo;
import com.example.apple.imdemo.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickContactActivity extends AppCompatActivity {

    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.lv_pick)
    ListView lvPick;

    private PickContactAdapter pickContactAdapter;

    private List<PickContactInfo> mPicks;
    private List<String> mExistMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {//隐藏标题栏
            getSupportActionBar().hide();
        }

        //获取传递过来数据
        getData();
        
        initData();

        initListener();
    }
    
    public void getData(){

        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId !=null ){
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);

            //获取群众已经存在的所有群成员

            mExistMembers = group.getMembers();

        }

        if (mExistMembers !=null){
            mExistMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        //listview条目选择
        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //CheckBox的切换
                CheckBox cb_pick = (CheckBox) view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());

                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setChecked(cb_pick.isChecked());

                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });

        //保存事件处理
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到已经选择的联系人
                List<String> names =  pickContactAdapter.getPickContacts();

                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members",names.toArray(new String[0]));//集合转string数组,String[0]参数是随意给的

                //设置返回的结果码
                setResult(RESULT_OK,intent);
                //结束当前页面
                finish();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        //从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        mPicks = new ArrayList<>();
        if (contacts!=null && contacts.size()>=0){

            //转换
            for (UserInfo contact : contacts){

                PickContactInfo pickContactInfo = new PickContactInfo(contact,false);
                mPicks.add(pickContactInfo);
            }
        }

        //初始化listview
        pickContactAdapter = new PickContactAdapter(this,mPicks,mExistMembers);
        lvPick.setAdapter(pickContactAdapter);
    }
}
