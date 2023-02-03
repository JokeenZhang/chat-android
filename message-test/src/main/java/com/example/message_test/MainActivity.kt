package com.example.message_test

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dylanc.viewbinding.binding

import com.example.message_test.R

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var viewModel: ChatViewModel
//    private val viewBinding: ActivityMainBinding by binding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        viewModel.login()

        findViewById<TextView>(R.id.tv_user).text =
            "当前用户：${viewModel.loginUser}\n聊天用户：${viewModel.toChatUser}"
        findViewById<Button>(R.id.btn_text_message).setOnClickListener(this)
        findViewById<Button>(R.id.btn_extension_message).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_text_message -> {
                viewModel.sendTextMessage()
            }
            //扩展字段消息
            R.id.btn_extension_message -> {
                viewModel.sendAttrMessage()
            }
        }
    }
}