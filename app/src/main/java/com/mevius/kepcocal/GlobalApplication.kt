package com.mevius.kepcocal

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    companion object {
        lateinit var instance: GlobalApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        KakaoSdk.init(this, "586b93d1ccc28ebac97b0e8953374b3c")
    }

    fun applicationContext(): Context = applicationContext
}