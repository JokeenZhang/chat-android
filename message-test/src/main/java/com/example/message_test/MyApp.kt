package com.example.message_test

import android.app.Application
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initHuanxin()
    }

    private fun initHuanxin() {
        /*
        APPKEY:1101221231166990#hensonmall2b-dev
        Client ID:YXA6-55T45wfTlWxz2QHQXl2xw
        ClientSecret:YXA6nCyffW_B4yDXwIkeCeO-g1e7WV4
         */
        val options = EMOptions()
        options.setAppKey("1124170418178691#mall-dev");
        options.autoLogin = true
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)

    }
}