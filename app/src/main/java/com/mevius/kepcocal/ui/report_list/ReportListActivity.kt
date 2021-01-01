package com.mevius.kepcocal.ui.report_list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.ui.report_cell_form_list.ReportCellFormListActivity
import com.mevius.kepcocal.ui.report_list.adapter.ReportRVAdapter
import com.mevius.kepcocal.utils.AndroidBug5497Workaround
import com.mevius.kepcocal.utils.FileManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_list.*


/**
 * [ReportListActivity]
 * 보고서(엑셀 파일) 리스트를 띄우고 리스트의 아이템을 추가 / 삭제할 수 있는 기능을 하는 Activity
 * ListView Item 이 클릭될 시 해당 보고서의 상세 정보 액티비티(지도 및 기기 리스트)인 ReportFormEditActivity 로 이동함
 * 길게 클릭 시 보고서 삭제 가능 ( with Dialog )
 */

@AndroidEntryPoint
class ReportListActivity : AppCompatActivity() {
    private val safRequestCode: Int = 42     // Request Code for SAF

    private lateinit var recyclerViewAdapter: ReportRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    private val reportListViewModel: ReportListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_list)

        setupUI()
        setupViewModel()
    }


    /**
     * NestedScrollView Height Test Code
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    Toast.makeText(this, scroll_view.height.toString(), Toast.LENGTH_SHORT).show()
    }
     */

    /**
     * [onActivityResult Method]
     * When SAF Intent Activity is terminated (Copy(Rename) File to App File Path)
     * 엑셀 파일 선택 후 원래 Activity 로 결과를 들고 돌아오며 onActivityResult 가 호출됨
     * 이때 Text Input 이 있는 Dialog 를 띄워 보고서 제목을 물어보고, 그걸 파일명으로 하여 앱 내부 저장공간에 저장
     * 저장은 OK 눌렀을 때만 이루어짐.
     * Cancel 누를 경우 아무것도 안함.
     */
    @SuppressLint("InflateParams")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == safRequestCode && resultCode == Activity.RESULT_OK) {     // When is SAF Request & Result is successful
            var reportTitleInput: String
            var reportIntervalInput: String
            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.dialog_with_two_edit_text, null)

            AlertDialog.Builder(this).apply {
                setTitle("프로젝트 추가")
                setView(viewInflated)
                setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    reportTitleInput =
                        viewInflated.findViewById<AutoCompleteTextView>(R.id.input1).text.toString()
                    reportIntervalInput =
                        viewInflated.findViewById<AutoCompleteTextView>(R.id.input2).text.toString()
                    data?.data?.also { uri ->
                        // 1. 파일 앱 내부 디렉토리에 별도 저장, 이후 뷰 모델로 전환...
                        val fileManager = FileManager()
                        val isXls = fileManager.saveFileAs(uri, reportTitleInput)

                        // 2. Database Report 추가
                        val report = Report(
                            null,
                            reportTitleInput,
                            reportIntervalInput.toInt(),
                            isXls
                        )
                        reportListViewModel.insertReport(report)
                        dialog.dismiss()
                    }
                }
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }      // If Click Cancel, Do Nothing
                show()
            }
        }
    }

    private fun setupUI() {
        AndroidBug5497Workaround.assistActivity(this)   // https://issuetracker.google.com/issues/36911528
        setupRecyclerView()
        setupFloatingActivityButton()
    }

    private fun setupRecyclerView() {
        /*
         * [RecyclerView Report Item onClick]
         * 아이템 클릭시 보고서 수정 액티비티로 넘어가기 위한 코드
         * 아이템을 선택하면 해당 보고서의 ReportFormEditActivity 로 넘어감.
         */
        val itemClick: (Long?) -> Unit = {
            val intent = Intent(this, ReportCellFormListActivity::class.java).apply {
                putExtra(
                    "reportId",
                    it
                )
            }
            startActivity(intent)
        }

        /*
         * [RecyclerView Report Item onLongClick]
         * 보고서 및 엑셀 파일 삭제를 위한 코드
         * 길게 눌러서 프로젝트 삭제 확인 다이얼로그 띄움
         */
        val itemLongClick: (Report) -> Boolean = {
            AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            ).apply {
                setTitle("보고서 삭제") //제목
                setMessage("정말로 삭제하시겠어요?")
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    // 1. 파일 삭제
                    // TODO FileManager Singleton?
                    val fileManager = FileManager()
                    val fileName = if (it.isXls) it.title + ".xls" else it.title + ".xlsx"
                    fileManager.removeFile(fileName)

                    // 2. DB에서 삭제
                    // TODO ViewModel deleteReport 안에 파일 삭제 로직 삽입하기
                    reportListViewModel.deleteReport(it)
                    dialog.dismiss()
                }
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }
                this.show()
            }
            true
        }

        // RecyclerView 설정
        recyclerViewAdapter = ReportRVAdapter(this, itemClick, itemLongClick)
        recyclerViewLayoutManager = LinearLayoutManager(this)
        rv_report_list.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        rv_report_list.layoutManager = recyclerViewLayoutManager
    }

    private fun setupFloatingActivityButton() {
        /*
         * [Floating Action Button onClickListener ]
         * 보고서(엑셀 파일) 추가를 위한 버튼
         * 누르면 SAF 를 통해 엑셀 파일을 선택할 수 있음
         * 파일을 선택하면 onActivityResult 액티비티로 넘어감.
         */
        fab_report_list.setOnClickListener {
            // Type of Target File
            val mimeTypes = arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            // Attach Excel File With SAF(Storage Access Framework)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                if (mimeTypes.isNotEmpty()) {
                    this.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            }
            startActivityForResult(
                intent,
                safRequestCode
            )   // Start Activity with RequestCode for onActivityResult
        }
    }

    private fun setupViewModel() {
        // ViewModel observe
        reportListViewModel.allReports.observe(this, { reports ->    // 초기 데이터 로드시에도 호출됨
            reports?.let {
                recyclerViewAdapter.setReports(it)
                iv_isEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        })
    }
}