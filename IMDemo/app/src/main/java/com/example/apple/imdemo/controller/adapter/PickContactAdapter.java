package com.example.apple.imdemo.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 2017/1/9.
 * <p>
 * 选择联系人的页面适配器
 */

public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();

    private List<String> mExistMembers = new ArrayList<>();//保存群中已经村子的成员集合

    public PickContactAdapter(Context context, List<PickContactInfo> picks,List<String> mExistMembers) {
        mContext = context;

        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }

        //加载已经存在的群成员
        mExistMembers.clear();
        mExistMembers.addAll(mExistMembers);
    }


    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //创建或获取ViewHolder
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = View.inflate(mContext, R.layout.item_pick, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //获取item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        //显示数据
        holder.tvPick.setText(pickContactInfo.getUser().getName());
        holder.cbPick.setChecked(pickContactInfo.isChecked());

        //判断
        if (mExistMembers.contains(pickContactInfo.getUser().getHxid())){
            holder.cbPick.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        //返回数据
        return convertView;
    }

    //获取选择的联系人
    public List<String> getPickContacts() {

        List<String> picks = new ArrayList<>();

        for (PickContactInfo pick : mPicks){
            //判断是否选中
            if (pick.isChecked()){
                picks.add(pick.getUser().getName());
            }
        }
        return picks;

    }

    static class ViewHolder {
        @BindView(R.id.cb_pick)
        CheckBox cbPick;
        @BindView(R.id.tv_pick)
        TextView tvPick;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
