package com.example.apple.imdemo.controller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.apple.imdemo.R;
import com.example.apple.imdemo.model.bean.InvationInfo;
import com.example.apple.imdemo.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 17/1/7.
 * <p>
 * 邀请信息列表页面的适配器
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvationInfo> mInvationInfos = new ArrayList<>();
    private OnInviteListener mOnInviteListener;

    private InvationInfo invationInfo;

    private final String TAG = "InviteAdapter";

    public InviteAdapter(Context context,OnInviteListener onInviteListener) {

        mContext = context;

        mOnInviteListener = onInviteListener;
    }

    // 刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos) {

        if (invationInfos != null && invationInfos.size() >= 0) {

            mInvationInfos.clear();

            mInvationInfos.addAll(invationInfos);

            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return mInvationInfos == null ? 0 : mInvationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建一个viewHolder

        ViewHolder holder = null;

        if (convertView == null){

            convertView = View.inflate(mContext, R.layout.item_invite, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //获取当前item数据
        invationInfo = mInvationInfos.get(position);

        //显示当前item数据
        UserInfo user = invationInfo.getUserInfo();
        if (user !=null){//联系人
            //名称显示
            holder.inviteName.setText(invationInfo.getUserInfo().getName());

            holder.inviteAccept.setVisibility(View.GONE);
            holder.inviteReject.setVisibility(View.GONE);

            // 原因
            if (invationInfo.getStatus() == InvationInfo.InvitationStatus.NEW_INVITE){//新的邀请

                if (invationInfo.getReason() == null){
                    holder.inviteReason.setText("添加好友");
                }else {
                    holder.inviteReason.setText(invationInfo.getReason()+ "++");
                }

                holder.inviteAccept.setVisibility(View.VISIBLE);
                holder.inviteReject.setVisibility(View.VISIBLE);
                Log.e(TAG,"新的邀请="+invationInfo.getStatus());

            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT){//接受邀请

                if (invationInfo.getReason() == null){
                    holder.inviteReason.setText("接受邀请");
                }else {
                    holder.inviteReason.setText(invationInfo.getReason()+"++");
                }

                Log.e(TAG,"接受邀请="+invationInfo.getStatus());
            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER){//邀请被接受

                if (invationInfo.getReason() == null){
                    holder.inviteReason.setText("邀请被接受");
                }else {
                    holder.inviteReason.setText(invationInfo.getReason()+"++");
                }
                Log.e(TAG,"邀请被接受="+invationInfo.getStatus());
            }

            //按钮处理
            holder.inviteAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"inviteAccept id ="+invationInfo.getUserInfo().getHxid());
                    mOnInviteListener.onAccept(invationInfo);//invationInfo当前item 数据
                }
            });

            //拒绝按钮点击事件处理
            holder.inviteReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invationInfo);
                }
            });


        }else {//群主
            //显示名称
            holder.inviteName.setText(invationInfo.getGroupInfo().getInvatePerson());

            holder.inviteAccept.setVisibility(View.GONE);
            holder.inviteReject.setVisibility(View.GONE);

            //显示原因
            switch (invationInfo.getStatus()){
                //您的群申请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.inviteReason.setText("您的群申请已经被接受");
                    break;

                //您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.inviteReason.setText("您的群邀请已经被接收");
                    break;

                //您的申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.inviteReason.setText("您的申请已经被拒绝");
                    break;

                //您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.inviteReason.setText("您的群邀请已经被拒绝");
                    break;

                //您收到了群邀请
                case  NEW_GROUP_INVITE:
                    holder.inviteReason.setText("您收到了群邀请");

                    holder.inviteAccept.setVisibility(View.VISIBLE);
                    holder.inviteReject.setVisibility(View.VISIBLE);
                    //接受邀请
                    holder.inviteAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invationInfo);
                        }
                    });

                    //拒绝邀请
                    holder.inviteReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invationInfo);
                        }
                    });
                    break;

                //您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.inviteReason.setText("您收到了群申请");

                    holder.inviteAccept.setVisibility(View.VISIBLE);
                    holder.inviteReject.setVisibility(View.VISIBLE);

                    //接受申请
                    holder.inviteAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invationInfo);
                        }
                    });
                    //拒绝申请
                    holder.inviteReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invationInfo);
                        }
                    });
                    break;

                //您接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.inviteReason.setText("您接受了群邀请");
                    break;

                //您批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    holder.inviteReason.setText("您批准了群加入");
                    break;
            }

        }

        //返回view


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.invite_name)
        TextView inviteName;
        @BindView(R.id.invite_reason)
        TextView inviteReason;
        @BindView(R.id.invite_accept)
        Button inviteAccept;
        @BindView(R.id.invite_reject)
        Button inviteReject;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnInviteListener{
        //联系人接受按钮的点击事件
        void onAccept(InvationInfo invationInfo);

        //联系人拒绝按钮的点击事件
        void onReject(InvationInfo invationInfo);

        //接受邀请按钮处理
        void onInviteAccept(InvationInfo invationInfo);

        //拒绝邀请按钮处理
        void onInviteReject(InvationInfo invationInfo);

        //接受申请按钮处理
        void onApplicationAccept(InvationInfo invationInfo);

        //拒绝申请按钮处理
        void onApplicationReject(InvationInfo invationInfo);
    }
}
