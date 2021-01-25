package com.mevius.kepcocal.ui.report_cell_form_list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.ui.report_cell_form_edit.ReportCellFormEditActivity
import com.mevius.kepcocal.ui.report_cell_form_list.adapter.ReportCellFormRVAdapter
import com.mevius.kepcocal.utils.AndroidBug5497Workaround
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_cell_form_list.*

/**
 * [ReportCellFormListActivity]
 * 보고서에 종속된 셀 데이터 리스트를 띄우고 리스트의 아이템을 추가 / 삭제할 수 있는 기능을 하는 Activity
 * ListView Item 이 클릭될 시 해당 보고서 셀 양식의 수정 액티비티인 ReportCellFormEditActivity 로 이동함
 * 길게 클릭 시 셀 양식 삭제 가능 ( with Dialog )
 */

@AndroidEntryPoint
class ReportCellFormListActivity : AppCompatActivity() {
    private var reportId = 0L
    private lateinit var recyclerViewAdapter: ReportCellFormRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    private val reportCellFormListViewModel: ReportCellFormListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_cell_form_list)

        getReportIdFromIntent()
        setupUI()
        setupViewModel()
    }

    /**
     * [getReportIdFromIntent]
     * Intent Extra 로부터 클릭된 ReportId 가져오기
     */
    private fun getReportIdFromIntent() {
        reportId = intent.getLongExtra("reportId", 0L)
        if (reportId == 0L) { // 만약 전달받은 fileName 이 Null 이라면 즉시 액티비티 종료
            Toast.makeText(this, "올바르지 않은 보고서입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupUI() {
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
            val intent = Intent(this, ReportCellFormEditActivity::class.java).apply {
                putExtra("reportId", reportId)
                putExtra("cellFormId", it)
            }
            startActivity(intent)
        }

        /*
         * [RecyclerView Report Item onLongClick]
         * 보고서 및 엑셀 파일 삭제를 위한 코드
         * 길게 눌러서 프로젝트 삭제 확인 다이얼로그 띄움
         */
        val itemLongClick: (CellForm) -> Boolean = {
            AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            ).apply {
                setTitle("셀 양식 삭제") //제목
                setMessage("정말로 삭제하시겠어요?")
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    reportCellFormListViewModel.deleteCellForm(it)
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
        recyclerViewAdapter = ReportCellFormRVAdapter(this, itemClick, itemLongClick)
        recyclerViewLayoutManager = LinearLayoutManager(this)
        rv_cell_form_list.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        rv_cell_form_list.layoutManager = recyclerViewLayoutManager
    }

    private fun setupFloatingActivityButton() {
        /*
         * [Floating Action Button onClickListener ]
         * 보고서(엑셀 파일) 추가를 위한 버튼
         */
        fab_report_cell_form_list.setOnClickListener {
            val intent = Intent(this, ReportCellFormEditActivity::class.java).apply {
                putExtra("reportId", reportId)
            }
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        // ViewModel observe
        reportCellFormListViewModel.getCellFormsWithReportId(reportId).observe(this, { cellForms ->    // 초기 데이터 로드시에도 호출됨
            cellForms?.let {
                recyclerViewAdapter.setCellForms(it)
                // iv_isEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        })
    }
}