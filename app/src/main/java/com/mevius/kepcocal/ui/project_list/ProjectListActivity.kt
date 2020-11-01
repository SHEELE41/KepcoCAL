package com.mevius.kepcocal.ui.project_list

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.ui.project_list.adapter.ProjectRVAdapter
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.MachineData
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.network.GeocoderAPI
import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import com.mevius.kepcocal.ui.project_detail.ProjectDetailActivity
import com.mevius.kepcocal.util.ComputerizedNumberCalculator
import com.mevius.kepcocal.util.ExcelParser
import kotlinx.android.synthetic.main.activity_project_list.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


/*
* [ProjectListActivity]
* 프로젝트(엑셀 파일) 리스트를 띄우고 리스트의 아이템을 추가 / 삭제할 수 있는 기능을 하는 Activity
* ListView Item 이 클릭될 시 해당 프로젝트의 상세 정보 액티비티(지도 및 기기 리스트)인 ProjectDetailActivity 로 이동함
*
* Floating Action Button 을 이용하여 프로젝트를 추가할 수 있으며 과정은 다음과 같음
* - SAF(Storage Access Framework)를 이용하여 엑셀 파일을 불러옴
* - Dialog 를 띄워 프로젝트명을 입력받음
* - 불러온 엑셀 파일의 파일명을 입력받은 프로젝트명으로 Rename 하여 앱 내부 저장공간인 /Android/data/com.mevius.kepcocal/files 에 복사
* - 파일명 = 프로젝트명 이므로 프로젝트 리스트를 띄울 때는 파일명 리스트만 읽으면 됨
*
*
* ListView 프로젝트 리스트 갱신
* 파일 목록 상의 파일명과 수정 날짜를 읽어서 ArrayList 에 갱신... 따로 클래스화하여 FAB onClick 이벤트에 추가
*/

class ProjectListActivity : AppCompatActivity(), CoroutineScope {
    private val safRequestCode: Int = 42     // Request Code for SAF
    private val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private lateinit var recyclerViewAdapter: ProjectRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    private lateinit var projectListViewModel: ProjectListViewModel
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var machineList = arrayListOf<MachineData>()    // 기기 정보 리스트 생성 (생성만 함)
    private val noCoordMachineArrayList =
        arrayListOf<MachineData>()    // 나중에 전산화번호 참조해서 마커 찍어줄 좌표 없는 객체들 모아놓는 ArrayList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_list)
        job = Job()
        /*
         * [RecyclerView Project Item onClick]
         * 아이템 클릭시 프로젝트 상세 액티비티로 넘어가기 위한 코드
         * 아이템을 선택하면 해당 프로젝트의 ProjectDetailActivity 로 넘어감.
         */
        val itemClick: (Long?) -> Unit = {
            val intent = Intent(this, ProjectDetailActivity::class.java).apply {
                putExtra(
                    "projectId",
                    it
                )
            }
            startActivity(intent)
        }

        /*
         * [RecyclerView Project Item onLongClick]
         * 프로젝트(엑셀 파일) 삭제를 위한 코드
         * 길게 눌러서 프로젝트 삭제 확인 다이얼로그 띄움
         */
        val itemLongClick: (Project) -> Boolean = {
            val builder: AlertDialog.Builder = AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            )
            builder.setTitle("프로젝트 삭제") //제목
            builder.setMessage("정말로 삭제하시겠어요?")
            builder.setPositiveButton("확인") { dialog, _ ->
                projectListViewModel.delete(it)
                dialog.dismiss()
            }
            builder.setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
            builder.show()
            true
        }

        // RecyclerView 설정
        recyclerViewAdapter = ProjectRVAdapter(this, itemClick, itemLongClick)
        recyclerViewLayoutManager = LinearLayoutManager(this)
        rv_project_list.adapter = recyclerViewAdapter   // Set Apapter to RecyclerView in xml
        rv_project_list.layoutManager = recyclerViewLayoutManager

        projectListViewModel = ViewModelProvider(this).get(ProjectListViewModel::class.java)
        projectListViewModel.allProjects.observe(this, { projects ->
            projects?.let {
                if (projects.isEmpty()){
                    iv_isEmpty.visibility = View.VISIBLE
                } else {
                    iv_isEmpty.visibility = View.INVISIBLE
                }
                recyclerViewAdapter.setProjects(it)
            }
        })

        /*
         * [Floating Action Button onClickListener ]
         * 프로젝트(엑셀 파일) 추가를 위한 버튼
         * 누르면 SAF 를 통해 엑셀 파일을 선택할 수 있음
         * 파일을 선택하면 onActivityResult 액티비티로 넘어감.
         */
        fab_project_list.setOnClickListener {

            // Type of Target File
            val mimeTypes = arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )

            // Attach Excel File With SAF(Storage Access Framework)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {      // Case of Higher Version than KITKAT
                    type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"     // All Files
                    if (mimeTypes.isNotEmpty()) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    }
                } else {
                    var mimeTypesStr = ""
                    for (mimeType in mimeTypes) {
                        mimeTypesStr += "$mimeType|"
                    }
                    type = mimeTypesStr.substring(
                        0,
                        mimeTypesStr.length - 1
                    )   // "application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
            }

            startActivityForResult(
                intent,
                safRequestCode
            )   // Start Activity with RequestCode for onActivityResult
        }
    }

    /*
     * [onActivityResult Method]
     * When SAF Intent Activity is terminated (Copy(Rename) File to App File Path)
     * 엑셀 파일 선택 후 원래 Activity 로 결과를 들고 돌아오며 onActivityResult 가 호출됨
     * 이때 Text Input 이 있는 Dialog 를 띄워 프로젝트명을 물어보고, 그걸 파일명으로 하여 앱 내부 저장공간에 저장
     * 저장은 OK 눌렀을 때만 이루어짐.
     * Cancel 누를 경우 아무것도 안함.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == safRequestCode && resultCode == Activity.RESULT_OK) {     // When Result is successful

            var projectNameInput: String

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Project Name")

            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.dialog_with_edit_text, null)

            builder.setView(viewInflated)

            builder.setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                projectNameInput =
                    viewInflated.findViewById<AutoCompleteTextView>(R.id.input).text.toString()
                data?.data?.also { uri ->
                    val project = Project(
                        null,
                        "$projectNameInput.xlsx",
                        todayDateFormat
                    )
                    projectListViewModel.insert(project)

                    val excelParser = ExcelParser(uri)
                    machineList = excelParser.excelToList()
                    launch {
                        displayInvalidAddrMachine()
                    }
                    dialog.dismiss()
                }
            }

            builder.setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }      // If Click Cancel, Do Nothing

            builder.show()      // Show Dialog
        }
    }


    private suspend fun displayMachinesLocation() {
        Log.d("###################################", "API 요청 시작")
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

                        if (machineData.coordinateLng != "" && machineData.coordinateLat != "") {   // 좌표가 둘 다 비어있지 않다면
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
        // 전산화번호 계산기 객체 선언
        val cNumberCalculator = ComputerizedNumberCalculator()

        // 좌표 정보가 존재하지 않는 기기들의 리스트를 순회하는 반복문
        for (noCoordMachine in noCoordMachineArrayList) {
            var closestDistance: Long = Long.MAX_VALUE  // 이 부분도 좀 고쳤으면.
            var closestMachine: MachineData? = null

            cNumberCalculator.targetNumber =
                noCoordMachine.computerizedNumber  // 한 순회마다 noCoordMachine 에 대한 전산화번호로 갱신

            // 위도 경도 정보가 제대로 존재하면서 noCoordMachine과 가장 가까운 기기를 찾기 위한 반복문
            for (machine in machineList) {
                cNumberCalculator.baseNumber =
                    machine.computerizedNumber   // 한 순회마다 machineList 안의 machine 에 대한 전산화번호로 갱신
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
                cNumberCalculator.baseNumber =
                    closestMachine.computerizedNumber    // for문을 계속 돌면서 마지막 machine의 값이 되어있을 것이므로 여기서는 갱신해줘야함.
                // 좌표 계산 루틴
                noCoordMachine.coordinateLng =
                    (closestMachine.coordinateLng.toDouble() + ((cNumberCalculator.getXDistance()
                        .toDouble() * 2.0) / (91290.0 + 85397.0))).toString()    //127
                noCoordMachine.coordinateLat =
                    (closestMachine.coordinateLat.toDouble() + ((cNumberCalculator.getYDistance()
                        .toDouble() * 2.0) / (110941.0 + 111034.0))).toString()  //37
            }
        }
    }
}