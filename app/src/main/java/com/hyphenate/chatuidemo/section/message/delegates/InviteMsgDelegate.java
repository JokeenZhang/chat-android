package com.hyphenate.chatuidemo.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage.InviteMessageStatus;

import androidx.annotation.NonNull;

public class InviteMsgDelegate extends NewFriendsMsgDelegate<InviteMessage, InviteMsgDelegate.ViewHolder> {
    private OnInviteListener listener;

    @Override
    public boolean isForViewType(InviteMessage msg, int position) {
        return msg.getStatusEnum() == InviteMessageStatus.BEINVITEED ||
                msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED ||
                msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_invite;
    }

    @Override
    protected InviteMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<InviteMessage> {
        private TextView name;
        private TextView message;
        private Button agree;
        private Button refuse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            agree = findViewById(R.id.agree);
            refuse = findViewById(R.id.refuse);
        }

        @Override
        public void setData(InviteMessage msg, int position) {
            name.setText(msg.getFrom());
            String reason = msg.getReason();
            if(TextUtils.isEmpty(reason)) {
                if(msg.getStatusEnum() == InviteMessageStatus.BEINVITEED){
                    reason = name.getContext().getResources().getString(R.string.Request_to_add_you_as_a_friend);
                }else if (msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED) { //application to join group
                    reason = name.getContext().getResources().getString(R.string.Apply_to_the_group_of) + msg.getGroupName();
                } else if (msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION) {
                    reason = name.getContext().getResources().getString(R.string.invite_join_group) + msg.getGroupName();
                }
            }
            message.setText(reason);

            agree.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteAgree(view, msg);
                }
            });

            refuse.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteRefuse(view, msg);
                }
            });
        }
    }

    public void setOnInviteListener(OnInviteListener listener) {
        this.listener = listener;
    }

    public interface OnInviteListener {
        void onInviteAgree(View view, InviteMessage msg);
        void onInviteRefuse(View view, InviteMessage msg);
    }
}
