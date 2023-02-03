package com.example.message_test

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.exceptions.HyphenateException

class ChatViewModel : ViewModel() {

//    val toChatUser = "zzqqqcom"
    val loginUser = "1501234569"
    val toChatUser = "1511234569"

    fun login() {
        EMClient.getInstance().login(loginUser, "1234567890", object : EMCallBack {
            override fun onSuccess() {
                Log.e("tetetetete", "login success 环信：success")
                EMClient.getInstance().chatManager().loadAllConversations()
                EMClient.getInstance().groupManager().loadAllGroups()
            }

            override fun onError(code: Int, error: String?) {
                Log.e("tetetetete", "login success 环信：onError $code, $error")
            }

        })

        EMClient.getInstance().chatManager().addMessageListener { list ->
            list.forEach { message ->
                Log.e("tetetetete", "消息 来自：${message.from}, body: ${message.body}")
                val attrId = try {
                    message.getStringAttribute("attr-id")
                } catch (e: HyphenateException) {
                    "null-null-null"
                }

                Log.e("tetetetete","带有扩展字段attr-id: $attrId")
            }
        }
    }

    /**
     * 普通的文本消息
     */
    fun sendTextMessage() {
        val message =
            EMMessage.createTextSendMessage("来自我的Demo ${System.currentTimeMillis()}", toChatUser)
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    /**
     * 扩展字段消息
     */
    fun sendAttrMessage() {
        val message =
            EMMessage.createTextSendMessage("来自我的Demo ${System.currentTimeMillis()}", toChatUser)
        message.setAttribute("attr-id", "90")
        EMClient.getInstance().chatManager().sendMessage(message)
    }
}