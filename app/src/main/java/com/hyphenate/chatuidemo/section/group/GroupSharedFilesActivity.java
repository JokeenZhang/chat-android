package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.group.adapter.SharedFilesAdapter;
import com.hyphenate.chatuidemo.section.group.viewmodels.SharedFilesViewModel;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.util.List;

public class GroupSharedFilesActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, OnRefreshLoadMoreListener, EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private static final int REQUEST_CODE_SELECT_FILE = 1;
    private static final int LIMIT = 20;
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private SharedFilesAdapter adapter;
    private SharedFilesViewModel viewModel;
    private int pageSize;
    private String groupId;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupSharedFilesActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_group_shared_files;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new SharedFilesAdapter();
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.demo_group_shared_files_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        EaseRecyclerView.RecyclerViewContextMenuInfo info = (EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.action_shared_delete :
                deleteFile(adapter.getItem(position));
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
        adapter.setOnItemClickListener(this);
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(SharedFilesViewModel.class);
        viewModel.getFilesObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMMucSharedFile>>() {
                @Override
                public void onSuccess(List<EMMucSharedFile> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                    finishLoadMore();
                }
            });
        });
        viewModel.getShowFileObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<File>() {
                @Override
                public void onSuccess(File data) {
                    openFile(data);
                }
            });
        });
        viewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    refresh();
                }
            });
        });
        refresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
       refresh();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        loadMore();
    }

    @Override
    public void onItemClick(View view, int position) {
        showFile(adapter.getItem(position));
    }

    private void refresh() {
        pageSize = LIMIT;
        viewModel.getSharedFiles(groupId, 0, pageSize);
    }

    private void loadMore() {
        pageSize += LIMIT;
        viewModel.getSharedFiles(groupId, 0, pageSize);
    }

    private void deleteFile(EMMucSharedFile file) {
        viewModel.deleteFile(groupId, file);
    }

    private void showFile(EMMucSharedFile item) {
        viewModel.showFile(groupId, item);
    }

    private void openFile(File file) {
        if(file != null && file.exists()) {
            EaseCompat.openFile(file, mContext);
        }
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            runOnUiThread(() -> srlRefresh.finishRefresh());
        }
    }

    private void finishLoadMore() {
        if(srlRefresh != null) {
            runOnUiThread(()-> srlRefresh.finishLoadMore());
        }
    }

    @Override
    public void onRightClick(View view) {
        selectFileFromLocal();
    }

    private void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_FILE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    viewModel.uploadFileByUri(groupId, uri);
                }
            }
        }
    }
}
