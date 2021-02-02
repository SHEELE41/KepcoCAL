package com.mevius.kepcocal.ui.report_cell_data_edit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.ui.report_cell_data_edit.adapter.ReportCellDataEditLVAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_cell_data_edit.*

@AndroidEntryPoint
class ReportCellDataEditActivity : AppCompatActivity() {
    private val reportCellDataEditViewModel: ReportCellDataEditViewModel by viewModels()
    private var projectId = 0L
    private var reportId = 0L
    private var machine: Machine? = null
    private val reportCellDataSet: HashMap<Int, CellData> = hashMapOf()
    private lateinit var listViewAdapter: ReportCellDataEditLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_cell_data_edit)

        getExtraFromIntent()
        setupViewModel()
        setupUI()
    }

    /**
     * [getExtraFromIntent]
     * Intent Extra 로부터 클릭된 ReportId 가져오기
     */
    private fun getExtraFromIntent() {
        projectId = intent.getLongExtra("projectId", 0L)
        reportId = intent.getLongExtra("reportId", 0L)
        machine = intent.getParcelableExtra("machine")
    }

    private fun setupUI() {
        setupListView()

        btn_report_cell_data_cancel.setOnClickListener {
            finish()
        }

        btn_report_cell_data_save.setOnClickListener {
            for (key in reportCellDataSet.keys) {
                reportCellDataEditViewModel.updateTransaction(reportCellDataSet[key]!!)
            }
            finish()
        }
    }

    private fun setupListView() {
        machine?.let {
            listViewAdapter =
                ReportCellDataEditLVAdapter(this, it, projectId, reportCellDataSet)
            lv_cell_data_edit.adapter = listViewAdapter
        }
    }

    private fun setupViewModel() {
        reportCellDataEditViewModel.getCellFormsWithReportId(reportId).observe(this, { cellForms ->
            cellForms?.let { listViewAdapter.setCellForms(it) }
        })
        reportCellDataEditViewModel.getSelectOptionDataWithReportId(reportId)
            .observe(this, { sodList ->
                sodList?.let { listViewAdapter.setSodList(it) }
            })
        reportCellDataEditViewModel.getCellDataWithMachineId(machine!!.id!!)
            .observe(this, { cellDataList ->
                cellDataList?.let { listViewAdapter.setCellDataList(it) }
            })
        reportCellDataEditViewModel.getReportWithId(reportId).observe(this, { itReport ->
            itReport?.let { listViewAdapter.setInterval(itReport.interval) }
        })
    }
}