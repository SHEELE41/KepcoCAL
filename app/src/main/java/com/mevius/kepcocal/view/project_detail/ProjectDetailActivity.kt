package com.mevius.kepcocal.view.project_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mevius.kepcocal.*
import com.mevius.kepcocal.data.MachineData
import com.mevius.kepcocal.data.network.GeocoderAPI
import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import com.mevius.kepcocal.util.ComputerizedNumberCalculator
import com.mevius.kepcocal.util.ExcelParser
import kotlinx.android.synthetic.main.activity_project_detail.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView  // ** Caution! import package
import kotlin.coroutines.CoroutineContext

/**
 * [ProjectDetailActivity]
 * 한 프로젝트(엑셀파일)에 해당되는 상세 정보가 담긴 액티비티
 * 구현 계획중인 기능
 * 1. 지도에 기기마다 마커 찍고 커스텀마크로 정보 표시
 * 2. 좀 힘들 것 같지만 그 Bottom Sheet로 ListView?
 */
class ProjectDetailActivity : AppCompatActivity(), MapView.POIItemEventListener , CoroutineScope {
    private val machineList = arrayListOf<MachineData>()    // 기기 정보 리스트 생성 (생성만 함)
    private val noCoordMachineArrayList =
        arrayListOf<MachineData>()    // 나중에 전산화번호 참조해서 마커 찍어줄 좌표 없는 객체들 모아놓는 ArrayList
    private lateinit var mapView: MapView
    private lateinit var job: Job
    private val VALID_MACHINE = 0
    private val INVALID_MACHINE = 1
    lateinit var bottomSheetBehavior : BottomSheetBehavior<View>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        job = Job()

        bottomSheetBehavior = BottomSheetBehavior.from(rl_bottom_sheet)

        // 1. Intent 작업
        // Intent 를 이용하여  ProjectListActivity 로부터 넘어온 fileName 을 수신
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
        MapView.setMapTilePersistentCacheEnabled(true)  // 맵뷰 캐시 사용

        val mapViewContainer = mapViewProjectDetail as ViewGroup
        mapViewContainer.addView(mapView)

        // 나중에 런타임 퍼미션 추가해주기
//        fab_project_detail.setOnClickListener() {
//            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
//        }

        floating_search_view.setOnQueryChangeListener { _: String, _: String ->
            @Override
            fun onSearchTextChanged(oldQuery : String, newQuery : String) {
                val newSearchSuggestions : List<SearchSuggestion> = listOf()

                floating_search_view.swapSuggestions(newSearchSuggestions)
            }
        }

        // 3. suspend function(비동기) displayMachineLocation 실행
        launch { displayMachinesLocation() }
    }

    // Activity와 Coroutine 생성주기를 맞춰주기 위해 onDestroy()에 job.cancel()
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * [displayMachinesLocation] function
     * API 통신을 이용하여 기기의 좌표 정보를 얻고 MapView에 마커를 추가하는 함수.
     */
    private suspend fun displayMachinesLocation() {
        Log.d("###################################", "API 요청 함수 시작")
        // api 객체 생성.
        // 어차피 같은 KakaoAPI, 그 중 Geocode API를 사용하므로 for 문 밖에 한번 선언해주는걸로 여러번 재활용 가능.
        // 함수 내부의 지역 변수이므로 함수 끝나면 싹 정리됨.
        val api = GeocoderAPI.create()

        var onResponseCounter = 0

        /*
        * [Coroutine]
        * 1. machineList 안의 machineData 하나를 가져옴
        * 2. 그 안의 address 1, 2 값을 검사하여 둘 중 하나라도 있으면 API 요청 대상 주소 machineAddr 값으로 설정
        * 3. 주소 둘 다 비어있으면 그 machineData 는 그냥 스킵. API 요청조차 하지 않음. (noCoordMachineArrayList 에 추가)
        * 4. 주소 있으면 해당 주소로 API 요청
        * 5. Response 에서 좌표 정보 빼와서 현재 machineData 객체의 x, y 필드에 set (어차피 해당 machineData 의 주소를 받아서 검색한거라 Index 신경쓸 필요는 없음)
        * 6. 좌표 조회 결과 유효하지 않으면 noCoordMachineArrayList 에 추가
        * 7. 부모 launch : machineList for 문을 감싸고 있으며, 이 for 문으로 여러개 생성된 각각의 자식 Coroutine 들이 모두 완료될 때 까지 대기.
        * 8. 자식 launch : for 문 안에서 machineList.size 개 만큼 실행되며, GeocoderAPI.getCoordinate()가 suspend 함수라서 Response 처리를 완료할 때까지 대기. (하나의 부모 Coroutine 이라고 할 수 있음)
        * 9. 자식 launch 의 범위는 꼭 마커 찍는 곳까지 묶어줘야 함.
        * 10. 모든 launch 다 끝나면(join) 좌표 누락된 noCoordMachineArrayList 의 기기들 좌표 계산해서 찍어줌.
        */
        coroutineScope {
            launch {    // 부모 코루틴은 생성된 자식 코루틴들이 모두 완료될 때 까지 대기
                for (machineData in machineList) {
                    // 비어있지 않은 주소를 machineAddr에 전달
                    val machineAddr: String = if (machineData.address1 != "") {
                        machineData.address1
                    } else if (machineData.address2 != "") {
                        machineData.address2
                    } else {
                        // 주소 둘 다 비어있으면 그냥 다음으로 넘어감
                        noCoordMachineArrayList.add(machineData)
                        continue
                    }

                    // Import 는 다 Retrofit2로 (Not OkHttp3!)
                    launch {
                        val response = api.getCoordinate(machineAddr)

                        // 각 launch에서 사용할 marker 인스턴스는 따로 생성해주어야 함.
                        // 여러 개의 Coroutine에서 동시에 marker에 정보를 set하려고 하면 다른 하나가 씹히는 현상 발생.
                        val marker = MapPOIItem()   // 선언
                        marker.setMarkerProperty(VALID_MACHINE)  // 마커 속성

                        if (response.isSuccessful) {
                            /*
                            * >> machineData에 좌표 데이터 넣어주기 전에 고려해야 할 것
                            * 1. Response의 documents 리스트가 비어있지는 않은가?
                            * 2. response.body()가 null은 아닌가?    // response 자체는 null이 아니지만 body는 가능성 존재
                            * 3. documents 자체가 null이 될 수도 있나...? X
                            */
                            val resultInstance: ResultGetCoordinate? = response.body()
                            if (resultInstance?.documents?.size != 0 && resultInstance != null) {
                                machineData.coordinateLng = resultInstance.documents[0].x.toString()
                                machineData.coordinateLat = resultInstance.documents[0].y.toString()
                            }
                        }

                        marker.itemName = machineData.computerizedNumber

                        if (machineData.coordinateLng != "" && machineData.coordinateLat != "") {   // 좌표가 둘 다 비어있지 않다면
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                                machineData.coordinateLat.toDouble(),
                                machineData.coordinateLng.toDouble()
                            )
                            Log.d("##################################","마커 참조")

                            mapView.addPOIItem(marker)
                            Log.d("##################################","맵뷰 참조")

                            if (onResponseCounter == 0) {   // 첫 번째 machineData에 대한 Response일 때
                                // 현재 마커의 위치를 중심점으로 설정 및 스케일 설정
                                mapView.setMapCenterPoint(
                                    marker.mapPoint, true
                                )
                                mapView.setZoomLevel(7, true)
                            }

                            // 마커 추가까지 정상적으로 완료된 개수
                            onResponseCounter++

                        } else {  // 좌표 하나라도 invalid 할 시 바로 좌표누락기기리스트에 넣어버림
                            noCoordMachineArrayList.add(machineData)
                        }
                    }  // for문마다 생기는 launch의 마지막 : 여기까지 한 작업의 단위로 묶어 비동기로 던져줘야 함.
                }
            }.join()    // 부모 launch 종료에 맞춤
            // 마찬가지로 비동기실행
            launch { displayInvalidAddrMachine() }
        }
    }

    /**
     * [displayInvalidAddrMachine] function
     * 좌표 정보가 유효하지 않은 기기들을 지도상에 표시하는 메소드
     * 좌표 정보가 유효한 가까운 기기를 기준점으로 하여 전산화번호 연산 후 자신의 좌표 도출
     */
    private fun displayInvalidAddrMachine() {
        Log.d("###################################", "문제있는 함수 시작")
        // 전산화번호 계산기 객체 선언
        val cNumberCalculator = ComputerizedNumberCalculator()

        // 좌표 정보가 존재하지 않는 기기들의 리스트를 순회하는 반복문
        for (noCoordMachine in noCoordMachineArrayList) {
            var closestDistance: Long = Long.MAX_VALUE  // 이 부분도 좀 고쳤으면.
            var closestMachine: MachineData? = null

            // 여기도 마찬가지로 각각의 장비에 대한 마커 따로 생성
            val marker = MapPOIItem()   // 선언
            marker.setMarkerProperty(INVALID_MACHINE)

            cNumberCalculator.targetNumber = noCoordMachine.computerizedNumber  // 한 순회마다 noCoordMachine 에 대한 전산화번호로 갱신

            // 위도 경도 정보가 제대로 존재하면서 noCoordMachine과 가장 가까운 기기를 찾기 위한 반복문
            for (machine in machineList) {
                cNumberCalculator.baseNumber = machine.computerizedNumber   // 한 순회마다 machineList 안의 machine 에 대한 전산화번호로 갱신
                if (machine.coordinateLat != "" && machine.coordinateLng != "") {    // 좌표 있는 기기 찾으면 둘 사이의 거리 계산(좌표 없는 기기 - 현재 기기)
                    // 정렬처럼 갈수록 더 짧은 거리로 갱신하면 되겠네
                    if (closestDistance > cNumberCalculator.getTotalDistance()) {
                        closestDistance = cNumberCalculator.getTotalDistance()
                        closestMachine = machine
                    }
                }
            }

            // 위도 경도 정보가 존재하는 가장 가까운 기기가 존재한다면 그 기기를 기준으로 좌표 계산 후 지도에 추가
            if (closestMachine != null) {
                Log.d(
                    "################################### 전산화번호 테스트",
                    noCoordMachine.computerizedNumber
                )
                cNumberCalculator.baseNumber =
                    closestMachine.computerizedNumber    // for문을 계속 돌면서 마지막 machine의 값이 되어있을 것이므로 여기서는 갱신해줘야함.
                // 좌표 계산 루틴
                noCoordMachine.coordinateLng =
                    (closestMachine.coordinateLng.toDouble() + ((cNumberCalculator.getXDistance()
                        .toDouble() * 2.0) / (91290.0 + 85397.0))).toString()    //127
                Log.d(
                    "좌표 테스트입니당",
                    "${noCoordMachine.lineName} ${noCoordMachine.lineNumber} ${noCoordMachine.coordinateLng}"
                )
                Log.d(
                    "가장 가까운 기기",
                    "${closestMachine.lineName} ${closestMachine.lineNumber} ${closestMachine.coordinateLng}"
                )
                Log.d(
                    "현재 차이값",
                    "${closestMachine.lineName} ${closestMachine.lineNumber} ${cNumberCalculator.getXDistance()}"
                )
                noCoordMachine.coordinateLat =
                    (closestMachine.coordinateLat.toDouble() + ((cNumberCalculator.getYDistance()
                        .toDouble() * 2.0) / (110941.0 + 111034.0))).toString()  //37
                Log.d("좌표 테스트입니당", noCoordMachine.coordinateLat)
                Log.d(
                    "가장 가까운 기기",
                    "${closestMachine.lineName} ${closestMachine.lineNumber} ${closestMachine.coordinateLat}"
                )
                Log.d(
                    "현재 차이값",
                    "${closestMachine.lineName} ${closestMachine.lineNumber} ${cNumberCalculator.getYDistance()}"
                )

                // 마커 정보 세팅 및 지도에 추가
                marker.itemName = noCoordMachine.computerizedNumber
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                    noCoordMachine.coordinateLat.toDouble(),
                    noCoordMachine.coordinateLng.toDouble()
                )
                mapView.addPOIItem(marker)
            }
        }
    }

    /**
     * [MapPOIItem.setMarkerProperty] function
     * 마커 속성 설정 확장함수
     * 인자로 마커 타입이 들어오면 그에 맞는 마커 속성 설정
     */
    private fun MapPOIItem.setMarkerProperty(machineType : Int) {
        this.showAnimationType = MapPOIItem.ShowAnimationType.DropFromHeaven  // 생성 시 애니메이션
        this.tag = 0  // 태그?
        when(machineType){
            0 -> {this.markerType = MapPOIItem.MarkerType.BluePin; this.selectedMarkerType = MapPOIItem.MarkerType.RedPin}  // VALID_MACHINE
            1 -> {this.markerType = MapPOIItem.MarkerType.RedPin; this.selectedMarkerType = MapPOIItem.MarkerType.BluePin}  // INVALID_MACHINE
            else -> {this.markerType = MapPOIItem.MarkerType.BluePin; this.selectedMarkerType = MapPOIItem.MarkerType.RedPin}   // DEFAULT
        }
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        TODO("Not yet implemented")
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        val onClickedMachineCNum = p1?.itemName?:""
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        tv_in_bottom_sheet.text = onClickedMachineCNum
        Log.d("###########################################","마커 말풍선 클릭")
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        TODO("Not yet implemented")
    }
}