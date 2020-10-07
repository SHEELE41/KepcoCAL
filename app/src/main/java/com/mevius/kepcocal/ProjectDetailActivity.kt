package com.mevius.kepcocal

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_project_detail.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView  // ** Caution! import package
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val arrayListJSON = ArrayListJSON()
        val jString = arrayListJSON.arrayListToJSON(machineList)
        Log.d("JSON STRING 출력해본다 ㅋㅋㅋㅋㅋ", jString)




        /**
         * 임시로 OKHttp 써볼까?
         * */

        val mapView = MapView(this)

        val mapViewContainer = mapViewProjectDetail as ViewGroup

        mapViewContainer.addView(mapView)

        val api = GeocoderAPI.create()

        // Import는 다 Retrofit으로
        api.getCoordinate("가운로2길").enqueue(object : Callback<ResultGetCoordinate> {
            override fun onResponse(
                call: Call<ResultGetCoordinate>,
                response: Response<ResultGetCoordinate>
            ) {
                Log.d("웹테스트트트트ㅡ틑트트트", "성공 !!!! ${response.raw()}")
            }

            override fun onFailure(call: Call<ResultGetCoordinate>, t: Throwable) {
                Log.d("웹테스트트트트ㅡ틑트트트", "실패 !!!! $t")
            }
        })

//        val markers = arrayOf<MapPOIItem>()
//
//        for ((index, machineData) in machineList.withIndex()){
//            markers[index].itemName = machineData.computerizedNumber
//            markers[index].tag = 0
//            markers[index].mapPoint
//            markers[index].markerType = MapPOIItem.MarkerType.BluePin       // 기본
//            markers[index].selectedMarkerType = MapPOIItem.MarkerType.RedPin       // 마커 클릭했을 때
//        }
//
//        mapView.addPOIItems(markers)

    }
}