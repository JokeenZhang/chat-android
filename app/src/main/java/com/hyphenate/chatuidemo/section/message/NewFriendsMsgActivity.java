package com.hyphenate.chatuidemo.section.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.message.delegates.AgreeMsgDelegate;
import com.hyphenate.chatuidemo.section.message.delegates.InviteMsgDelegate;
import com.hyphenate.chatuidemo.section.message.delegates.OtherMsgDelegate;
import com.hyphenate.chatuidemo.section.message.viewmodels.NewFriendsViewModel;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage.InviteMessageStatus;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class NewFriendsMsgActivity extends BaseInitActivity implements OnRefreshLoadMoreListener, InviteMsgDelegate.OnInviteListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private static final int limit = 10;
    private int offset;
    private NewFriendsMsgAdapter adapter;
    private NewFriendsViewModel viewModel;
    private InviteMsgDelegate msgDelegate;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, NewFriendsMsgActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_new_friends_msg;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new NewFriendsMsgAdapter();
        msgDelegate = new InviteMsgDelegate();
        adapter.addDelegate(new AgreeMsgDelegate())
                .addDelegate(msgDelegate)
                .addDelegate(new OtherMsgDelegate());
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.demo_invite_list_menu, menu);
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
        InviteMessage item = adapter.getItem(position);
        InviteMessage.InviteMessageStatus statusEnum = item.getStatusEnum();
        if(statusEnum == InviteMessageStatus.BEINVITEED ||
                statusEnum == InviteMessageStatus.BEAPPLYED ||
                statusEnum == InviteMessageStatus.GROUPINVITATION) {
            menu.findItem(R.id.action_invite_agree).setVisible(true);
            menu.findItem(R.id.action_invite_refuse).setVisible(true);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo()).position;
        InviteMessage message = adapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.action_invite_agree :
                viewModel.agreeInvite(message);
                break;
            case R.id.action_invite_refuse :
                viewModel.refuseInvite(message);
                break;
            case R.id.action_invite_delete :
                viewModel.deleteMsg(message);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
        msgDelegate.setOnInviteListener(this);
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewFriendsViewModel.class);
        viewModel.inviteMsgObservable().observe(this, response -> {
            finishRefresh();
            if(response == null) {
                return;
            }
            adapter.setData(response);
        });
        viewModel.moreInviteMsgObservable().observe(this, response -> {
            finishLoadMore();
            if(response == null) {
                return;
            }
            adapter.addData(response);
        });

        viewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    viewModel.loadMessages(limit);
                }
            });
        });
        viewModel.makeAllMsgRead();
        viewModel.loadMessages(limit);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        offset += limit;
        viewModel.loadMoreMessages(limit, offset);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.loadMessages(limit);
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.finishRefresh();
        }
    }

    private void finishLoadMore() {
        if(srlRefresh != null) {
            srlRefresh.finishLoadMore();
        }
    }

    @Override
    public void onInviteAgree(View view, InviteMessage msg) {
        viewModel.agreeInvite(msg);
    }

    @Override
    public void onInviteRefuse(View view, InviteMessage msg) {
        viewModel.refuseInvite(msg);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
