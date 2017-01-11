package com.example.apple.imdemo.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apple.imdemo.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 2017/1/9.
 * <p>
 * 群组列表的适配器
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;

    private List<EMGroup> mGrops = new ArrayList<>();

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    //刷新方法
    public void refresh(List<EMGroup> groups) {

        if (groups != null && groups.size() >= 0) {
            mGrops.clear();

            mGrops.addAll(groups);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGrops == null ? 0 : mGrops.size();
    }

    @Override
    public Object getItem(int position) {
        return mGrops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //创建或获取viewholder
        ViewHolder holder = null;

        if (convertView == null) {

            convertView = View.inflate(mContext, R.layout.item_grouplist, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();

        }

        //获取当前item数据
        EMGroup emGroup = mGrops.get(position);

        //显示数据
        holder.tvGroupList.setText(emGroup.getGroupName());

        //返回数据

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_groupList)
        TextView tvGroupList;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
