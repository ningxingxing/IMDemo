package com.example.apple.imdemo.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 2017/1/10.
 */

public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;

    private boolean mIsCanModify;//是否允许添加和删除群成员

    private List<UserInfo> mUsers = new ArrayList<>();

    private boolean mIsDeleteMode;//删除模式，true，可以删除，否则不可删除

    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify,OnGroupDetailListener OnGroupDetailListener) {
        mContext = context;

        mIsCanModify = isCanModify;

        mOnGroupDetailListener = OnGroupDetailListener;
    }

    // 获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteMode;
    }

    // 设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteMode = mIsDeleteModel;
    }


    //刷新数据
    public void refresh(List<UserInfo> users) {

        if (users != null && users.size() >= 0) {

            mUsers.clear();

            //添加加减号
            initUsers();

            mUsers.addAll(0, users);
        }
        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //获取或创建viewHolder
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = View.inflate(mContext, R.layout.item_group_detail, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取item数据
        final UserInfo userInfo = mUsers.get(position);

        //显示数据
        if (mIsCanModify) {//群主或开放了群权限

            //布局处理
            if (position == getCount() -1){//减号处理

                //删除模式判断
                if (mIsDeleteMode){
                    convertView.setVisibility(View.INVISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    holder.ivGroupDetailPhoto.setImageResource(R.mipmap.jian);
                    holder.ivGroupDetailDelete.setVisibility(View.GONE);
                    holder.tvGroupDetailName.setVisibility(View.INVISIBLE);
                }

            }else if (position == getCount() -2){//加号处理

                //删除模式判断
                if (mIsDeleteMode){
                    convertView.setVisibility(View.INVISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    holder.ivGroupDetailPhoto.setImageResource(R.mipmap.jia);
                    holder.ivGroupDetailDelete.setVisibility(View.GONE);
                    holder.tvGroupDetailName.setVisibility(View.INVISIBLE);
                }
            }else {//群成员

                convertView.setVisibility(View.VISIBLE);
                holder.tvGroupDetailName.setVisibility(View.VISIBLE);

                holder.tvGroupDetailName.setText(userInfo.getName());
                holder.ivGroupDetailPhoto.setImageResource(R.mipmap.ic_launcher);//显示默认头像

                if (mIsDeleteMode){
                    holder.ivGroupDetailDelete.setVisibility(View.VISIBLE);
                }else {
                    holder.ivGroupDetailDelete.setVisibility(View.GONE);
                }
            }

            //点击事件处理
            if (position == getCount() -1){//减号

                holder.ivGroupDetailPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteMode){
                            mIsDeleteMode = true;
                            notifyDataSetChanged();
                        }
                    }
                });

            }else if (position == getCount() -2){//加号

                holder.ivGroupDetailPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }else {
                holder.ivGroupDetailDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onDeleteMenber(userInfo);
                    }
                });
            }


        } else {//普通群成员

            if (position == getCount() -1 || position == getCount() -2){//-1 减号位置

                convertView.setVisibility(View.GONE);
            }else {
                convertView.setVisibility(View.VISIBLE);

                //名称
                holder.tvGroupDetailName.setText(userInfo.getName());

                //头像
                holder.ivGroupDetailPhoto.setBackgroundResource(R.mipmap.ic_launcher);

                //删除
                holder.ivGroupDetailDelete.setVisibility(View.GONE);
            }

        }


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_group_detail_photo)
        ImageView ivGroupDetailPhoto;
        @BindView(R.id.tv_group_detail_name)
        TextView tvGroupDetailName;
        @BindView(R.id.iv_group_detail_delete)
        ImageView ivGroupDetailDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnGroupDetailListener{
        //添加群成员方法
        void onAddMembers();

        //删除群成员方法
        void onDeleteMenber(UserInfo user);

    }
}
