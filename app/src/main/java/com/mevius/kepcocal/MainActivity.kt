package com.mevius.kepcocal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kakao.sdk.common.KakaoSdk
import kotlinx.android.synthetic.main.activity_main.*

import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_menu1.setOnClickListener() {
            var intent = Intent(this, ProjectListActivity::class.java)
            startActivity(intent)
        }

        btn_menu2.setOnClickListener() {
            var intent = Intent(this, ProjectDetailActivity::class.java)
            startActivity(intent)
        }

        btn_menu3.setOnClickListener() {
            var intent = Intent(this, ProjectListActivity::class.java)
            startActivity(intent)
        }
    }
}