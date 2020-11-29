package com.mevius.kepcocal.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

import com.mevius.kepcocal.ui.project_detail.ProjectDetailActivity
import com.mevius.kepcocal.ui.project_list.ProjectListActivity
import com.mevius.kepcocal.R
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_menu1.setOnClickListener() {
            val intent = Intent(this, ProjectListActivity::class.java)
            startActivity(intent)
        }

        btn_menu2.setOnClickListener() {
            val intent = Intent(this, ProjectDetailActivity::class.java)
            startActivity(intent)
        }

        btn_menu3.setOnClickListener() {
            val intent = Intent(this, ProjectListActivity::class.java)
            startActivity(intent)
        }
    }
}