package com.mevius.kepcocal

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_project_detail.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView  // ** Caution! import package
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ProjectDetailActivity
 * 한 프로젝트(엑셀파일)에 해당되는 상세 정보가 담긴 액티비티
 * 구현 계획중인 기능
 * 1. 지도에 기기마다 마커 찍고 커스텀마크로 정보 표시
 * 2. 좀 힘들 것 같지만 그 Bottom Sheet로 ListView?
 */
class ProjectDetailActivity : AppCompatActivity() {
    private val machineList = arrayListOf<MachineData>()    // 기기 정보 리스트 생성 (생성만 함)
    lateinit var mapView: MapView
    private val TAG: String = "ProjectDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        // 1. Intent 작업
        // Intent 를 이용하여  ProjectListActivity 로부터 넘어온 fileName 을 수신
        val intent = intent
        val fileName = intent.getStringExtra("fileName")

        // 만약 전달받은 fileName 이 Null 이라면 즉시 액티비티 종료
        if (fileName == null) {
            Toast.makeText(this, "올바르지 않은 프로젝트입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 수신받은 fileName 을 이용하여 엑셀 파일로부터 데이터를 읽어와 ArrayList 에 저장
        val excelParser = ExcelParser()     // Excel Parser 선언
        excelParser.excelToList(fileName!!, machineList)    // machineList 에 정상적으로 정보 이동 완료


        // 2. MapView 띄우기
        mapView = MapView(this)
        val mapViewContainer = mapViewProjectDetail as ViewGroup
        mapViewContainer.addView(mapView)


        // 3. API 통신을 이용하여 machineList 안의 machineData마다 좌표 필드 set.
        displayMachinesLocation()
    }


    /**
     * setCoordinateData() function
     * API 통신을 이용하여 machineList 안의 machineData마다 좌표 필드 정보 입력해주는 함수
     * 각 machineData마다 Callback 함수 onResponse가 할당되며, 모든 onResponse 함수 실행이 완료되어야 비로소 작업이 완료된 것이라고 할 수 있음
     * 따라서 함수 실행되자마자 로딩중 화면 띄우고 끝날 때까지 대기
     * 끝났는지 안 끝났는지는 remainCallback 변수를 이용하여 판단.
     */
    private fun displayMachinesLocation() {
        // api 객체 생성.
        // 어차피 같은 KakaoAPI, 그 중 Geocode API를 사용하므로 for 문 밖에 한번 선언해주는걸로 여러번 재활용 가능.
        // 함수 안에 넣음으로써 함수 끝나면 지역 변수 싹 정리됨.
        val api = GeocoderAPI.create()

        // 모든 Callback 수행 완료 여부 따지기 위한 Int형 변수
        // 초기값은 리스트의 크기로, for문 진행되면서 --
        // 0이 되는 순간 모든 아이템에 대한 Callback 완료된 것임.
        var machineCounter = 0

        var onResponseCounter = 0


        // 나중에 전산화번호 참조해서 마커 찍어줄 좌표 없는 객체들 모아놓는 ArrayList
        val noCoordMachineArrayList = arrayListOf<MachineData>()


        // marker 객체 기본값 세팅
        // for문 안에서 계속 생성시킬 바에는 밖에서 하나 생성해두고 재활용하자.
        // 객체를 참조하는 형태이므로 주소값은 변하지 않고 따라서 val로 선언이 가능.
        val marker = MapPOIItem()   // 선언
        marker.showAnimationType = MapPOIItem.ShowAnimationType.DropFromHeaven  // 생성 시 애니메이션
        marker.tag = 0  // 태그?
        marker.markerType = MapPOIItem.MarkerType.BluePin       // 기본
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin       // 마커 클릭했을 때

        /*
        * [Routine]
        * 1. machineList 안의 machinData 하나를 가져옴
        * 2. 그 안의 address 1, 2 값을 검사하여 둘 중 하나라도 있으면 API 요청 대상 주소 machineAddr 값으로 설정
        * 3. 주소 둘 다 비어있으면 그 machineData는 그냥 스킵. API 요청조차 하지 않음.
        * 4. 주소 있으면 해당 주소로 API 요청
        * 5. Response에서 좌표 정보 빼와서 현재 machineData 객체의 x, y 필드에 set (어차피 해당 machineData의 주소를 받아서 검색한거라 Index 신경쓸 필요는 없음)
        */
        for (machineData in machineList) {
            // 비어있지 않은 주소를 machineAddr에 전달
            val machineAddr: String = if (machineData.address1 != "") {
                machineData.address1
            } else if (machineData.address2 != "") {
                machineData.address2
            } else {
                // 주소 둘 다 비어있으면 그냥 다음으로 넘어감
                // 해당 machineData 객체는 주소, 좌표 필드 그냥 빈칸(초기값)으로 남아있음
                // 아무것도 안하긴 하지만 처리한 기계 대수 ++
                // 주소 없는 기계가 마지막에 있으면 좀 별로일 것 같은데... Response 완료 다 되고 ++ 되어봤자 if 문은 이미 지나감...
                machineCounter++
                noCoordMachineArrayList.add(machineData)
                continue
            }

            /*
            * 요청을 보내고 Response 처리는 Callback으로 받음
            * 그래서 Callback만 엄청 생성하고 displayMachinesLocation() 함수는 종료
            * 데이터 채워지는건 Response를 받은 이후임.
            * Response 들어왔을 때 콜백함수에서 좌표 입력 + 마커 찍기
            */
            // >>>>>>>>>>>> Import는 다 Retrofit2로!
            api.getCoordinate(machineAddr)
                .enqueue(object : Callback<ResultGetCoordinate> {     // Callback 인터페이스
                    override fun onResponse(
                        call: Call<ResultGetCoordinate>,
                        response: Response<ResultGetCoordinate>
                    ) {
                        /*
                        * >> machineData에 좌표 데이터 넣어주기 전에 고려해야 할 것
                        * 1. Response의 documents 리스트가 비어있지는 않은가?
                        * 2. response.body()가 null은 아닌가?    // response 자체는 null이 아니지만 body는 가능성 존재
                        * 3. documents 자체가 null이 될 수도 있나...?
                        */
                        val resultInstance: ResultGetCoordinate? = response.body()
                        if (resultInstance?.documents?.size != 0 && resultInstance != null) {
                            machineData.coordinateLng = resultInstance.documents[0].x.toString()
                            machineData.coordinateLat = resultInstance.documents[0].y.toString()
                        }

                        // 데이터 채워짐과 동시에 지도에 마커 추가
                        // 나중에 코루틴으로 구현 생각해보기
                        // 일단은 이게 베스트?
                        marker.itemName = machineData.computerizedNumber

                        if (machineData.coordinateLng != "" || machineData.coordinateLat != "") {   // 좌표가 둘 다 비어있지 않다면
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                                machineData.coordinateLat.toDouble(),
                                machineData.coordinateLng.toDouble()
                            )

                            mapView.addPOIItem(marker)
                            if (onResponseCounter == 0) {   // 첫 번째 machineData에 대한 Response일 때
                                // 현재 마커의 위치를 중심점으로 설정 및 스케일 설정
                                mapView.setMapCenterPoint(
                                    marker.mapPoint, true
                                )
                                mapView.setZoomLevel(7, true)
                            }

                            // 마커 추가까지 정상적으로 완료된 개수
                            onResponseCounter++
                        }
                        else {  // 좌표 하나라도 invalid 할 시 바로 좌표누락기기리스트에 넣어버림
                            noCoordMachineArrayList.add(machineData)
                        }



                        // 기계 대수 ++
                        machineCounter++

                        // 최종 아이템인지 확인
                        // 최종 아이템 또한 isnoCoord 확인을 거쳤으므로 리스트에 포함되어 있음
                        if (machineCounter == machineList.size){
                            val computerizedNumberCalculator = ComputerizedNumberCalculator()
                            for (noCoordMachine in noCoordMachineArrayList){
                                var closestDistance : Long = Long.MAX_VALUE
                                var closestMachine : MachineData? = null
                                for (machine in machineList){
                                    if (noCoordMachine == machine){
                                        continue
                                    }

                                    if (machine.coordinateLat != "" && machine.coordinateLng != ""){    // 좌표 있음
                                        // 정렬처럼 갈수록 더 짧은 거리로 갱신하면 되겠네
                                        if (closestDistance > computerizedNumberCalculator.getTotalDistance(machine.computerizedNumber, noCoordMachine.computerizedNumber)){
                                            closestDistance = computerizedNumberCalculator.getTotalDistance(machine.computerizedNumber, noCoordMachine.computerizedNumber)
                                            closestMachine = machine
                                        }
                                    }
                                }
                                // 대충 거리차이로 좌표 구하고 지도에 찍는다는 코드
                                if (closestMachine != null){
                                    noCoordMachine.coordinateLng = (closestMachine.coordinateLng.toDouble() + ((computerizedNumberCalculator.getXDistance(closestMachine.computerizedNumber, noCoordMachine.computerizedNumber).toDouble() * 2.0) / (91290.0 + 85397.0))).toString()    //127
                                    Log.d("좌표 테스트입니당", "${noCoordMachine.lineName} ${noCoordMachine.lineNumber} ${noCoordMachine.coordinateLng}")
                                    Log.d("가장 가까운 기기", "${closestMachine.lineName} ${closestMachine.lineNumber} ${closestMachine.coordinateLng}")
                                    Log.d("현재 차이값", "${closestMachine.lineName} ${closestMachine.lineNumber} ${computerizedNumberCalculator.getXDistance(closestMachine.computerizedNumber, noCoordMachine.computerizedNumber)}")
                                    noCoordMachine.coordinateLat = (closestMachine.coordinateLat.toDouble() + ((computerizedNumberCalculator.getYDistance(closestMachine.computerizedNumber, noCoordMachine.computerizedNumber).toDouble() * 2.0) / (110941.0 + 111034.0))).toString()  //12
                                    Log.d("좌표 테스트입니당", noCoordMachine.coordinateLat)
                                    marker.itemName = noCoordMachine.computerizedNumber
                                    marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                                        noCoordMachine.coordinateLat.toDouble(),
                                        noCoordMachine.coordinateLng.toDouble()
                                    )
                                    Log.d("왜 안되는겨?", "ㄹㅇ루")
                                    mapView.addPOIItem(marker)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResultGetCoordinate>, t: Throwable) {
                        Log.d(TAG, "실패 !!!! $t")
                    }
                })
        }
    }
}