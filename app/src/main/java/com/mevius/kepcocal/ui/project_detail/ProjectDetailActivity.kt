package com.mevius.kepcocal.ui.project_detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.ui.project_detail.adapter.SearchResultsListAdapter
import com.mevius.kepcocal.ui.project_detail.data.FSVDataHelper
import com.mevius.kepcocal.ui.project_detail.data.MachineSuggestion
import com.mevius.kepcocal.ui.report_cell_data_edit.ReportCellDataEditActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_project_detail.*
import kotlinx.android.synthetic.main.dialog_with_edit_text.view.*
import kotlinx.android.synthetic.main.project_detail_bottom_sheet.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * [ProjectDetailActivity]
 * 한 프로젝트에 해당되는 상세 정보가 담긴 액티비티
 */

@AndroidEntryPoint
class ProjectDetailActivity : AppCompatActivity(), MapView.MapViewEventListener,
    MapView.POIItemEventListener {
    private val permissionRequestCode = 1001
    private val isLocationApiOnRequestCode = 1002
    private val findSuggestionSimulatedDelay = 250L
    private var mLastQuery = ""
    private var reportId: Long = 0L
    private var report: Report? = null
    private var projectId: Long = 0L
    private var project: Project? = null
    private var isFABOpen = false
    private var isMapViewCenterDone = false
    private var viewModelActionFlag = true
    private var reportList = listOf<Report>()
    private var machineList = listOf<Machine>()
    private var cellDataList = listOf<CellData>()
    private lateinit var mapView: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val projectDetailViewModel: ProjectDetailViewModel by viewModels()
    private lateinit var searchResultsList: RecyclerView
    private lateinit var searchResultAdapter: SearchResultsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        getExtraFromIntent()
        setupViewModel()
        setupUI()
    }

    /**
     * [getExtraFromIntent]
     * Intent Extra 로부터 클릭된 ProjectId, ReportId 가져오기
     */
    private fun getExtraFromIntent() {
        intent.getParcelableExtra<Project>("project")?.let {
            project = it
            projectId = it.id ?: 0L
        }
        if (project == null) { // 만약 전달받은 객체가 Null 이라면 즉시 액티비티 종료
            Toast.makeText(this, "올바르지 않은 프로젝트입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Parcelize 버그. 생성자에 선언되지 않은 필드는 null 로 전달됨.
        // 따로 전달해주어야 하는 듯.
        reportId = intent.getLongExtra("reportId", 0L)
    }

    /**
     * [setupUI]
     * UI 종합 설정
     */
    private fun setupUI() {
        setupMapView()
        setupBottomSheet()
        setupFloatingActionButton()
        setupFloatingSearch()
        setupResultList()
    }

    /**
     * [setupBottomSheet]
     * BottomSheet 설정
     */
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        // BottomSheet 내부 버튼 설정
        btn_write_report.setOnClickListener {
            if (reportId == 0L) {    // reportId is null
                Toast.makeText(this, "보고서 연동이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val selectedMachine = machineList.find { it.machineIdInExcel == bs_tv_index.text }
                val mIntent = Intent(this, ReportCellDataEditActivity::class.java).apply {
                    putExtra("projectId", projectId)
                    putExtra("reportId", reportId)
                    putExtra("machine", selectedMachine)
                }
                startActivity(mIntent)
            }
        }

        btn_work_done.setOnClickListener {
            viewModelActionFlag = false
            val selectedMachine = machineList.find { it.machineIdInExcel == bs_tv_index.text }
            val selectedPOIItem =
                mapView.findPOIItemByTag(selectedMachine?.machineIdInExcel?.toInt()!!)
            selectedMachine.isDone = !selectedMachine.isDone    // toggle
            mapView.removePOIItem(selectedPOIItem)
            mapView.addPOIItem(MapPOIItem().setMarkerProperty(selectedMachine))
            projectDetailViewModel.update(selectedMachine)
        }
    }

    /**
     * [setupFloatingActionButton]
     * FloatingActionButton 설정
     * SpeedDial 라이브러리 사용하면 HTTP 에러 남 (Daum Map API 충돌)
     * TODO 후에 서브 버튼 옆에 Label 추가하기
     */
    @SuppressLint("InflateParams")
    private fun setupFloatingActionButton() {
        fab_project_detail_main.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        fab_project_detail_sub1.setOnClickListener {
            // TODO 매번 ArrayAdapter 새로 만드는게 부하를 주진 않을까? 차라리 전역변수로 돌리고 재활용?
            // TODO AlertDialog Title Margin 너무 거슬리는데...
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
            for (report in reportList) {
                adapter.add(report.title)
            }    // addAll String

            AlertDialog.Builder(this).apply {
                setTitle("보고서 연동")
                setAdapter(adapter) { _, which ->
                    reportList[which].id?.let { reportId = it }     // 현재 액티비티에서 사용할 reportId 변경
                    project?.let {
                        it.reportId = reportList[which].id
                        projectDetailViewModel.update(it)
                        Toast.makeText(
                            this@ProjectDetailActivity,
                            reportList[which].title + " 보고서가 연동되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                show()
            }
        }

        fab_project_detail_sub2.setOnClickListener {
            // 현재 프로젝트에 귀속된 모든 CellData 를 긁어와서 엑셀 파일로 내보내기
            var outputFileName: String
            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.dialog_with_edit_text, null)
            val textInputLayout = viewInflated.text_input_layout
            val autoCompleteTextView = viewInflated.input
            autoCompleteTextView.hint = "저장될 보고서 파일명을 입력해주세요."
            textInputLayout.hint = "이미 같은 이름의 파일이 존재할 경우 덮어쓰기됩니다."

            val mAlertDialogBuilder = AlertDialog.Builder(this).apply {
                setTitle("보고서 작성")
                setView(viewInflated)
                setPositiveButton(android.R.string.ok, null)
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }      // If Click Cancel, Do Nothing
            }

            // OK 버튼 눌렀을 때 항상 dismiss 되는 것을 원하지 않으므로 여기서 재설정
            val mAlertDialog = mAlertDialogBuilder.create()
            mAlertDialog.setOnShowListener { dialog ->
                val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                mPositiveButton.setOnClickListener {
                    outputFileName =
                        autoCompleteTextView.text.toString()
                    if (outputFileName == "") {
                        Toast.makeText(
                            this@ProjectDetailActivity,
                            "저장할 파일명을 입력해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        projectDetailViewModel.writeReportExcel(
                            cellDataList,
                            report,
                            outputFileName
                        )
                        dialog.dismiss()
                    }
                }
            }

            mAlertDialog.show()
        }
    }

    /**
     * [showFABMenu]
     * FAB Menu 펼치기
     * */
    private fun showFABMenu() {
        isFABOpen = true
        fab_project_detail_sub1.animate()
            .translationY(-resources.getDimension(R.dimen.standard_110))
        fab_project_detail_sub2.animate().translationY(-resources.getDimension(R.dimen.standard_60))
    }

    /**
     * [closeFABMenu]
     * FAB Menu 숨기기
     * */
    private fun closeFABMenu() {
        isFABOpen = false
        fab_project_detail_main.bringToFront()  // Sub 버튼 클릭 후 닫으면 Sub 버튼이 위로 올라오는 현상 방지
        fab_project_detail_sub1.animate().translationY(0F)
        fab_project_detail_sub2.animate().translationY(0F)
    }

    /**
     * [onBackPressed]
     * Back 키 눌렀을 때 FAB Menu 닫을 수 있도록 override
     * */
    override fun onBackPressed() {
        if (!isFABOpen) {
            super.onBackPressed()
        } else {
            closeFABMenu()
        }
    }

    /**
     * [setupMapView]
     * MapView 설정
     */
    private fun setupMapView() {
        mapView = MapView(this)
        mapView.setMapViewEventListener(this)   // MapViewEventListener Binding
        mapView.setPOIItemEventListener(this)   // POIItemEventListener Binding
        MapView.setMapTilePersistentCacheEnabled(true)  // Use MapView Cache
        val mapViewContainer = mapViewProjectDetail as ViewGroup
        mapViewContainer.addView(mapView)
    }

    /**
     * [setupViewModel]
     * ViewModel 및 Observer 설정
     */
    private fun setupViewModel() {
        projectDetailViewModel.getMachinesWithProjectId(projectId).observe(this, { machines ->
            machines?.let {
                if (viewModelActionFlag) {
                    mapView.removeAllPOIItems()
                    for (machine in machines) {
                        val marker = MapPOIItem().setMarkerProperty(machine)
                        mapView.addPOIItem(marker)
                    }
                    // Center 설정 되어있지 않다면 실행
                    // 액티비티 전반적으로 한 번만 실행되어야 함.
                    if (!isMapViewCenterDone) {
                        mapView.apply {
                            if (poiItems.isNotEmpty()) {
                                setMapCenterPoint(
                                    poiItems[0].mapPoint, true
                                )
                                setZoomLevel(7, true)
                            }
                        }
                        isMapViewCenterDone = true
                    }
                }
            }
            FSVDataHelper.sLiveMachineData = machines
            machineList = machines
            viewModelActionFlag = true
        })

        // ReportId 연동
        projectDetailViewModel.getProjectWithId(projectId).observe(this, { project ->
            project?.let {
                reportId = it.reportId ?: 0L
            }
        })

        projectDetailViewModel.allReports.observe(this, { reports ->
            reports?.let {
                reportList = reports
            }
        })

        projectDetailViewModel.getCellDataWithProjectId(projectId).observe(this, { cellDataList ->
            cellDataList?.let {
                this.cellDataList = cellDataList
            }
        })

        projectDetailViewModel.getReportWithId(reportId).observe(this, { report ->
            report?.let {
                this.report = report
            }
        })
    }

    /**
     * [setupFloatingSearch]
     * FloatingSearchView에 각종 이벤트 연결
     */
    private fun setupFloatingSearch() {
        val floatingSearchView = floating_search_view

        // SearchView에 입력중인 문자열 변경 이벤트
        floatingSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                floatingSearchView.clearSuggestions()
            } else {
                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                floatingSearchView.showProgress()
                FSVDataHelper.findSuggestions(newQuery, 5, findSuggestionSimulatedDelay,
                    object : FSVDataHelper.OnFindSuggestionsListener {
                        override fun onResults(results: List<MachineSuggestion?>?) {
                            floatingSearchView.swapSuggestions(results)
                            floatingSearchView.hideProgress()
                        }
                    })
            }
        }

        // 검색 결과 이벤트
        floatingSearchView.setOnSearchListener(
            object : FloatingSearchView.OnSearchListener {
                // 방식 1 : Suggestion 중 하나 클릭
                override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                    val machineSuggestion = searchSuggestion as MachineSuggestion
                    FSVDataHelper.findMachines(
                        machineSuggestion.body,
                        object : FSVDataHelper.OnFindMachinesListener {
                            override fun onResults(results: List<Machine?>?) {
                                // 검색 창 닫기
                                floatingSearchView.clearSearchFocus()
                                if (!results.isNullOrEmpty()) {
                                    results[0]?.let {
                                        mapView.apply {
                                            val resultMarker =
                                                findPOIItemByTag(it.machineIdInExcel.toInt())
                                            setBottomSheetData(it)
                                            expandBottomSheet()
                                            setMapCenterPoint(
                                                resultMarker.mapPoint, true
                                            )
                                            setZoomLevel(2, true)
                                            selectPOIItem(resultMarker, true)
                                        }
                                    }
                                }
                            }
                        })
                    // 검색 창에 남아있을 마지막 쿼리 문자열
                    mLastQuery = searchSuggestion.body.toString()
                }

                // 방식 2 : 텍스트 입력 후 정직하게 검색 버튼 클릭 (사용 안함)
                override fun onSearchAction(currentQuery: String?) {
                    if (currentQuery != null) {
                        mLastQuery = currentQuery
                    }
                    FSVDataHelper.findMachines(
                        currentQuery,
                        object : FSVDataHelper.OnFindMachinesListener {
                            override fun onResults(results: List<Machine?>?) {
                                searchResultAdapter.swapData(results)
                            }
                        })
                }
            }
        )

        // FloatingSearchView 우측 메뉴 아이콘 이벤트
        floatingSearchView.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_location) {  // 근데 그 중 위치 버튼이 눌렸을 때
                // 위치 권한 및 기능 on/off 확인 후 현위치 Tracking
                checkSettingAndTracking()
            }
        }

        // onFocus 이벤트
        floatingSearchView.setOnFocusChangeListener(
            object : FloatingSearchView.OnFocusChangeListener {
                override fun onFocus() {
                    floatingSearchView.swapSuggestions(
                        FSVDataHelper.getHistory(
                            this@ProjectDetailActivity,
                            3
                        )
                    )
                }

                override fun onFocusCleared() {
                    floatingSearchView.setSearchBarTitle(mLastQuery)
                }
            }
        )
    }

    /**
     * [setupResultList]
     * FloatingSearchView 결과 RecyclerView 설정
     */
    private fun setupResultList() {
        searchResultsList = search_results_list
        searchResultAdapter = SearchResultsListAdapter()
        searchResultsList.adapter = searchResultAdapter
        searchResultsList.layoutManager = LinearLayoutManager(this)
    }

    /**
     * [expandBottomSheet]
     * BottomSheet 펼치기
     */
    private fun expandBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * [collapseBottomSheet]
     * BottomSheet 내리기
     */
    private fun collapseBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /**
     * [setBottomSheetData]
     * BottomSheet DataMapping
     * TODO : 후에 DataBinding 사용 예정
     */
    @SuppressLint("SetTextI18n")
    private fun setBottomSheetData(machine: Machine) {
        btn_work_done.isChecked = machine.isDone
        bs_tv_line_name_number.text = machine.lineName + " " + machine.lineNumber
        bs_tv_index.text = machine.machineIdInExcel
        bs_tv_computerized_number.text = machine.computerizedNumber
        bs_tv_address.text =
            if (machine.isNoCoord) "주소 데이터가 유효하지 않아 계산된 위치에 찍힌 핀입니다." else if (machine.address1 != "") machine.address1 else machine.address2
        bs_tv_manufacturing_data.text =
            machine.company + " " + machine.manufacturingNumber + " (" + machine.manufacturingYear + "." + machine.manufacturingDate + ")"
    }

    /**
     * [checkSettingAndTracking]
     * 1. Runtime Location Permission Check
     * 2. Location API State On/Off Check
     * 3. If conditions aren't satisfied -> show dialogs
     * 4. If conditions are satisfied -> tracking present location
     */
    private fun checkSettingAndTracking() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {   // If permission is granted
            // Check Location API State is ON
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }  // ACCESS_FINE_LOCATION
            val lsqBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> =
                client.checkLocationSettings(lsqBuilder.build())

            // If State is ON
            task.addOnSuccessListener {
                if (it.locationSettingsStates.isLocationPresent) {
                    // Start Tracking
                    mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                }
            }

            // If State is OFF
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            this@ProjectDetailActivity,
                            isLocationApiOnRequestCode
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        } else {    // If permission isn't granted
            // RequestPermissions (show dialog)
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionRequestCode
            )
        }
    }

    /**
     * [onRequestPermissionsResult]
     * 위치 기능 켜짐 요청 Dialog 선택 결과에 따른 루틴들이 모여있는 함수 (Only ViewLogic!!)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            // Start Tracking
            Activity.RESULT_OK -> mapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            // When User Choose Cancel -> Toast
            Activity.RESULT_CANCELED -> Toast.makeText(this, "위치 기능이 꺼져있습니다.", Toast.LENGTH_SHORT)
                .show()
            else -> Toast.makeText(this, "위치 기능이 꺼져있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * [onRequestPermissionsResult]
     * 권한 요청 Dialog 선택 결과에 따른 루틴들이 모여있는 함수
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // 위치 권한에 관한 결과. when 조건 추가해줌으로써 다른 권한 허용도 같은 함수에서 따낼 수 있음
            permissionRequestCode -> {
                if (grantResults.isEmpty()) {
                    // 그럴 일은 없겠지만 결과가 비어있는 경우
                    throw RuntimeException("Empty Permission Result")
                }

                // 사용자가 Dialog 에서 허용을 눌러 제대로 권한 부여가 된 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Start Tracking
                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                } else {    // 한 번 허용 눌렀을 때
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            permissionRequestCode
                        )
                    } else {    // 사용자가 Dialog 에서 다시 묻지 않음을 눌렀는데 다시 요청이 들어온 경우
                        showDialogToGetPermission()
                    }
                }
            }
        }
    }


    /**
     * [showDialogToGetPermission]
     * 사용자가 Dialog 에서 다시 묻지 않음을 눌렀는데 다시 요청이 들어온 경우
     * 이미 설정이 다시는 묻지 않는 것이 되어버렸기 때문에 사용자가 앱 설정창에 가서 직접 허용해야 함
     */
    private fun showDialogToGetPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("권한 요청")
            .setMessage(
                "해당 앱의 기능을 수행하기 위해서는 위치 권한이 필요합니다. " +
                        "위치 권한을 허용해주시기 바랍니다."
            )

        // 허용 누르면 앱 설정창으로 이동
        builder.setPositiveButton("확인") { _, _ ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)   // 6
        }
        // 나중에 누르면 또 무시(아무 것도 안 함)
        builder.setNegativeButton("나중에") { _, _ ->
            // ignore
        }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * [MapPOIItem.setMarkerProperty] function
     * 마커 속성 설정 확장함수
     * 인자로 기기 엔티티가 들어오면 그에 맞는 마커 속성 설정
     */
    private fun MapPOIItem.setMarkerProperty(machine: Machine): MapPOIItem {
        showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround  // 생성 시 애니메이션
        tag = machine.machineIdInExcel.toInt()  // 각 마커의 태그는 엑셀에서의 연번
        markerType =
            if (machine.isDone) MapPOIItem.MarkerType.BluePin else MapPOIItem.MarkerType.RedPin
//         customImageResourceId =
//             if (machine.isDone) R.drawable.ic_machine_complete else if (machine.isNoCoord) R.drawable.ic_machine_incomplete_no_coord else R.drawable.ic_machine_incomplete
//        customSelectedImageResourceId =
//             if (machine.isDone) R.drawable.ic_machine_complete_selected else if (machine.isNoCoord) R.drawable.ic_machine_incompleted_no_coord_selected else R.drawable.ic_machine_incomplete_selected
//        setCustomImageAnchor(0.5f, 1.0f)
        itemName = machine.computerizedNumber
        mapPoint = MapPoint.mapPointWithGeoCoord(
            machine.coordinateLat.toDouble(),
            machine.coordinateLng.toDouble()
        )
        return this
    }

    /**
     * [onPOIItemSelected] function
     * 마커 클릭 시 호출되는 함수.
     * 마커 클릭 시마다 마커에 해당되는 기기의 정보를 BottomSheet 에 매핑
     */
    override fun onPOIItemSelected(mMapView: MapView?, mMapPOIItem: MapPOIItem?) {
        val onClickedPOIItemTag = mMapPOIItem?.tag
        val onClickedMachine: Machine?

        // 선택된 마커에 대한 기기 찾기
        onClickedMachine = machineList.find { it.machineIdInExcel.toInt() == onClickedPOIItemTag }

        // 기기의 데이터를 뷰에 매핑
        onClickedMachine?.let {
            setBottomSheetData(it)
        }
    }

    // Deprecated
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    /**
     * [onCalloutBalloonOfPOIItemTouched]
     * 마커 터치하면 나오는 말풍선을 터치했을 때 호출되는 함수
     * BottomSheet 내려가있으면 올려줌
     */
    override fun onCalloutBalloonOfPOIItemTouched(
        mMapView: MapView?,
        mMapPOIItem: MapPOIItem?,
        mCalloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType?
    ) {
        // BottomSheet 숨겨져있으면 열기
        expandBottomSheet()
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    /**
     * [onMapViewSingleTapped] function
     * MapView 자체가 한번 터치되었을 때 호출되는 함수
     * BottomSheet 올라가있으면 내려줌
     */
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        // 맵 클릭 시 BottomSheet 내리기
        collapseBottomSheet()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }
}