package com.mevius.kepcocal

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_project_detail.*
import net.daum.mf.map.api.MapView  // ** Caution! import package

class ProjectDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        val mapView = MapView(this)

        val mapViewContainer = mapViewProjectDetail as ViewGroup

        mapViewContainer.addView(mapView)
    }
}