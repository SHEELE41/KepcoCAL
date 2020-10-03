package com.mevius.kepcocal

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_project_detail.*
import net.daum.mf.map.api.MapView  // ** Caution! import package

class ProjectDetailActivity : AppCompatActivity() {
    private val machineList = arrayListOf<MachineData>()    // 기기 리스트


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        /*
        * Intent 를 이용하여  ProjectListActivity 로부터 넘어온 fileName 을 수신
        */
        val intent = intent
        val fileName = intent.getStringExtra("fileName")

        // 만약 전달받은 fileName 이 Null 이라면 즉시 액티비티 종료
        if (fileName == null) {
            Toast.makeText(this, "올바르지 않은 프로젝트입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        /*
        * 수신받은 fileName 을 이용하여 엑셀 파일로부터 데이터를 읽어와 ArrayList 에 저장
        */
        val excelParser = ExcelParser()     // Excel Parser 선언
        excelParser.excelToList(fileName!!, machineList)    // machineList 에 정상적으로 정보 이동 완료




        val mapView = MapView(this)

        val mapViewContainer = mapViewProjectDetail as ViewGroup

        mapViewContainer.addView(mapView)
    }
}