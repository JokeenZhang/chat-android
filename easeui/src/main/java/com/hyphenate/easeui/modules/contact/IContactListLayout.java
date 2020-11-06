package com.hyphenate.easeui.modules.contact;

import android.view.View;

import com.hyphenate.easeui.modules.conversation.EaseConversationPresenter;
import com.hyphenate.easeui.modules.conversation.delegate.EaseBaseConversationDelegate;
import com.hyphenate.easeui.modules.interfaces.IRecyclerView;

public interface IContactListLayout extends IRecyclerView {

    /**
     * 添加其他类型的代理类
     * @param delegate
     */
    //void addDelegate(EaseBaseConversationDelegate delegate);

    /**
     * 设置presenter
     * @param presenter
     */
    //void setPresenter(EaseConversationPresenter presenter);

    /**
     * 是否展示默认的条目菜单
     * @param showDefault
     */
    void showItemDefaultMenu(boolean showDefault);

}
