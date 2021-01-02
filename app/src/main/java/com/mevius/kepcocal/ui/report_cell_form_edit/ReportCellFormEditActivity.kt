package com.mevius.kepcocal.ui.report_cell_form_edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.ui.report_cell_form_edit.adapter.TypeTwoRVAdapter
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeOne
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeThree
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeTwo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_cell_form_edit.*
import kotlinx.android.synthetic.main.report_cell_form_edit_type2.*
import kotlinx.android.synthetic.main.report_cell_form_edit_type3.*

/**
 * [ReportCellFormEditActivity]
 * 셀 양식의 이름, 타입, 간격 및 선택 데이터를 수정하는 기능을 하는 Activity
 */

@AndroidEntryPoint
class ReportCellFormEditActivity : AppCompatActivity() {
    private var reportId = 0L
    private var cellFormId = 0L
    private lateinit var recyclerViewAdapter: TypeTwoRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
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
        reportId = intent.getLongExtra("reportId", 0L)
        cellFormId = intent.getLongExtra("cellFormId", 0L)
    }


    private fun setupUI() {
        setupFragments()
        type2_btn_add.setOnClickListener {
            val selectOptionData = SelectOptionData(
                null,
                cellFormId,
                type2_select_option_data_input.text.toString()
            )
            reportCellFormEditViewModel.insertSelectOptionData(selectOptionData)
        }

        btn_report_cell_form_save.setOnClickListener {
            val checkedRadioButtonIndex = when(rg_report_cell_form_edit.checkedRadioButtonId){
                R.id.radio_btn1 -> 1
                R.id.radio_btn2 -> 2
                R.id.radio_btn3 -> 3
                else -> 1
            }

            val cellForm = CellForm(
                cellFormId,
                reportId,
                input_cell_form_name.text.toString(),
                checkedRadioButtonIndex,
                input_first_cell.text.toString()
            )

            reportCellFormEditViewModel.insertCellForm(cellForm)

            finish()
        }
        setupRecyclerView()
        setupSpinner()
    }

    private fun setupFragments() {
        showTypeOneFragment()   // Default
        radio_btn1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTypeOneFragment()
            }
        }
        radio_btn2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTypeTwoFragment()
            }
        }
        radio_btn3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTypeThreeFragment()
            }
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

    /*
    * TYPE 2 : 선택 입력
    * */
    private fun setupRecyclerView() {
        // RecyclerView Btn Onclick
        val itemBtnClick: (SelectOptionData) -> Unit = {
            reportCellFormEditViewModel.deleteSelectOptionData(it)
        }

        // RecyclerView 설정
        recyclerViewAdapter = TypeTwoRVAdapter(this, itemBtnClick)
        recyclerViewLayoutManager = LinearLayoutManager(this)
        rv_type2.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        rv_type2.layoutManager = recyclerViewLayoutManager
    }

    /*
    * TYPE 3 : 자동 입력
    * */
    @SuppressLint("ResourceType")
    private fun setupSpinner() {
        // 저장되어있는 값에 따라 스피너 체크된 것 달라지도록
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_labels_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            type3_spinner.adapter = adapter
        }
    }

    private fun setupViewModel() {
        if (cellFormId != 0L) {
            reportCellFormEditViewModel.getCellFormWithId(cellFormId).observe(this, { cellForm ->
                cellForm?.let {
                    input_cell_form_name.setText(it.name)
                    input_first_cell.setText(it.firstCell)
                    when (it.type) {
                        // 중복 체크가 가능한가?
                        1 -> radio_btn1.isChecked = true
                        2 -> radio_btn2.isChecked = true
                        3 -> radio_btn3.isChecked = true
                    }
                }
            })
            reportCellFormEditViewModel.getSelectOptionDataWithCellFormId(cellFormId)
                .observe(this, { selectOptionData ->
                    recyclerViewAdapter.setSelectOptionData(selectOptionData)
                })
        } else {
            // TODO 단순히 마지막 인덱스를 가져오는 것이므로 Count 이용...
            cellFormId = 1L // 첫 번째 양식 추가일 때
            reportCellFormEditViewModel.getCellFormsWithReportId(reportId).observe(this, { cellForms ->
                cellForms?.let {
                    cellFormId = (it.lastIndex + 2).toLong()
                }
            })
        }
    }
}