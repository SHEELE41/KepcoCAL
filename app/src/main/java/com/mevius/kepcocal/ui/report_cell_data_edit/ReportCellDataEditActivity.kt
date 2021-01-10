package com.mevius.kepcocal.ui.report_cell_data_edit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.ui.report_cell_data_edit.adapter.ActivityRVAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_cell_data_edit.*

@AndroidEntryPoint
class ReportCellDataEditActivity : AppCompatActivity() {
    private val reportCellDataEditViewModel: ReportCellDataEditViewModel by viewModels()
    private var reportId = 0L
    private var machine: Machine? = null
    private lateinit var recyclerViewAdapter: ActivityRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_cell_data_edit)

        getExtraFromIntent()
        setupUI()
        setupViewModel()
    }

    /**
     * [getExtraFromIntent]
     * Intent Extra 로부터 클릭된 ReportId 가져오기
     */
    private fun getExtraFromIntent() {
        reportId = intent.getLongExtra("reportId", 0L)
        machine = intent.getParcelableExtra("machine")
    }

    private fun setupUI() {
        setupRecyclerView()

        // 저장 버튼 눌렀을 때
        // TODO 레이아웃 수정
    }

    private fun setupRecyclerView() {
        machine?.let {
            recyclerViewAdapter = ActivityRVAdapter(this, it)
            recyclerViewLayoutManager = LinearLayoutManager(this)
            rv_cell_data_edit.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
            rv_cell_data_edit.layoutManager = recyclerViewLayoutManager
        }
    }

    private fun setupViewModel() {
        reportCellDataEditViewModel.getCellFormsWithReportId(reportId).observe(this, { cellForms ->
            cellForms?.let {
                recyclerViewAdapter.setCellForms(it)
            }
        })
        reportCellDataEditViewModel.allSelectOptionData.observe(this, { sodList ->
            sodList?.let {
                recyclerViewAdapter.setSodList(it)
            }
        })
    }
}