package com.mevius.kepcocal.ui.report_cell_form_edit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeOne
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeThree
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeTwo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_cell_form_edit.*

/**
 * [ReportCellFormEditActivity]
 * 셀 양식의 이름, 타입, 간격 및 선택 데이터를 수정하는 기능을 하는 Activity
 */

@AndroidEntryPoint
class ReportCellFormEditActivity : AppCompatActivity() {
    private val reportCellFormEditViewModel: ReportCellFormEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_cell_form_edit)

        getExtraDataFromIntent()
        setupUI()
        setupViewModel()
    }

    /**
     * [getExtraDataFromIntent]
     * Intent Extra 로부터 해당 ReportId, CellFormId 가져오기
     */
    private fun getExtraDataFromIntent() {
        reportCellFormEditViewModel.reportId = intent.getLongExtra("reportId", 0L)
        reportCellFormEditViewModel.cellFormId = intent.getLongExtra("cellFormId", 0L)
    }


    private fun setupUI() {
        setupFragments()

        btn_report_cell_form_save.setOnClickListener {
            val checkedRadioButtonIndex = when(rg_report_cell_form_edit.checkedRadioButtonId){
                R.id.radio_btn1 -> 1
                R.id.radio_btn2 -> 2
                R.id.radio_btn3 -> 3
                else -> 1
            }

            val cellForm = CellForm(
                reportCellFormEditViewModel.cellFormId,
                reportCellFormEditViewModel.reportId,
                input_cell_form_name.text.toString(),
                checkedRadioButtonIndex,
                input_first_cell.text.toString()
            )

            reportCellFormEditViewModel.updateTransaction(cellForm)

            finish()
        }
    }

    private fun setupFragments() {
        showTypeOneFragment()   // Default
        radio_btn1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { showTypeOneFragment() }
        }
        radio_btn2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { showTypeTwoFragment() }
        }
        radio_btn3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { showTypeThreeFragment() }
        }
    }

    private fun showTypeOneFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.inflate_parent_layout, FragmentTypeOne()).commit()
    }

    private fun showTypeTwoFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.inflate_parent_layout, FragmentTypeTwo()).commit()
    }

    private fun showTypeThreeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.inflate_parent_layout, FragmentTypeThree()).commit()
    }

    private fun setupViewModel() {
        if (reportCellFormEditViewModel.cellFormId != 0L) { // 기존 데이터를 수정하는 경우
            reportCellFormEditViewModel.getCellFormWithId(reportCellFormEditViewModel.cellFormId).observe(this, { cellForm ->
                cellForm?.let {
                    input_cell_form_name.setText(it.name)
                    input_first_cell.setText(it.firstCell)
                    when (it.type) {
                        1 -> radio_btn1.isChecked = true
                        2 -> radio_btn2.isChecked = true
                        3 -> radio_btn3.isChecked = true
                    }
                }
            })
        } else { // 새 데이터를 추가할 경우
            // TODO 단순히 마지막 인덱스를 가져오는 것이므로 Count 이용...
            reportCellFormEditViewModel.cellFormId = 1L // 첫 번째 양식 추가일 때

            // 굳이 LiveData 일 필요?
            // 처음엔 아무것도 안 들어가있다가 값 초기화되면 바뀔 수 있도록...?
            reportCellFormEditViewModel.lastCellForm.observe(this, { lastCellForm ->
                lastCellForm?.let {
                    reportCellFormEditViewModel.cellFormId = it.id!!.plus(1L)
                }
            })
        }
    }
}