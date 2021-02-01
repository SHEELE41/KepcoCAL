package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import kotlinx.android.synthetic.main.report_cell_data_edit_type1_rv_item.view.*
import kotlinx.android.synthetic.main.report_cell_data_edit_type2_rv_item.view.*
import kotlinx.android.synthetic.main.report_cell_data_edit_type3_rv_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReportCellDataEditLVAdapter(
    private val context: Context,
    private val machine: Machine,
    private val projectId: Long,
    private val dataSet: HashMap<Int, CellData>
): BaseAdapter() {
    private var interval = 0
    private var cellFormList = emptyList<CellForm>()
    private var sodList = emptyList<SelectOptionData>()
    private var cellDataList = emptyList<CellData>()
    private val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewType = getItemViewType(position)
        var itemView: View? = null
        when (viewType) {
            1 -> {  // 직접 입력
                // View Inflate
                itemView = LayoutInflater.from(context).inflate(R.layout.report_cell_data_edit_type1_rv_item, null)

                // Inflate 된 View 에서 각 요소를 추출
                val tvCellFormName: TextView? = itemView.tv_cell_form_name_t1
                val editText: AutoCompleteTextView? = itemView.input_cell_data_content_t1

                // 텍스트 변경 사항을 기록하기 위한 리스너 선언 및 position 설정
                val mCustomEditTextListener = CustomEditTextListener().apply { updatePosition(position) }
                editText?.addTextChangedListener(mCustomEditTextListener)   // 리스너 연결

                // 현재 position 에 대한 cellForm 을 이용하여 데이터 매핑
                val cellForm = getItem(position)
                tvCellFormName?.text = cellForm.name

                var initContent = ""
                var cellDataId: Long? = null
                val cell = calculateCellLocation(cellForm.firstCell)

                // 이전에 저장된 값 있다면 editTextView 에 채워넣기
                // TextWatcher 는 텍스트가 바뀔 때만 반응하므로 수정 없이 저장을 눌렀을 때에 대비하여 initContent 초기화
                // cellDataId 값으로 Database UPDATE 가능하도록 함.
                for (cellData in cellDataList) {
                    if (cellData.cellFormId == cellForm.id) {
                        initContent = cellData.content
                        cellDataId = cellData.id
                    }
                }

                // 우선 DB 에서 불러온 cellData 값을 먼저 집어넣되, 방금 저장된 초기값("")이 아닌 따끈따끈한 값이 있다면 그걸로 바꿈
                // 첫 입력에서 화면을 벗어났다가 다시 돌아올 경우 값이 사라지는 상황을 방지하기 위해.
                if (dataSet[position] != null && dataSet[position]?.content != "") {
                    initContent = dataSet[position]?.content.toString()
                }
                editText?.setText(initContent)

                // bind 될 때 기본적으로 CellData 객체를 position 에 생성
                dataSet[position] = CellData(
                    cellDataId,
                    projectId,
                    machine.id,
                    cellForm.id,
                    initContent,
                    cell
                )
            }
            2 -> {  // 선택 입력
                // View Inflate
                itemView = LayoutInflater.from(context).inflate(R.layout.report_cell_data_edit_type2_rv_item, null)

                // Inflate 된 View 에서 각 요소를 추출
                val tvCellFormName: TextView? = itemView.tv_cell_form_name_t2
                val radioGroup: RadioGroup? = itemView.radio_group_inner_item_type2

                // 현재 position 에 대한 cellForm 을 이용하여 데이터 매핑
                val cellForm = getItem(position)
                tvCellFormName?.text = cellForm.name

                var cellData: CellData? = null
                val cell = calculateCellLocation(cellForm.firstCell)

                // 저장된 데이터 불러오기
                // cellDataList 자체가 machineId(primary) 를 기준으로 불러온 데이터라 겹칠 일 없음
                // ex) 다른 프로젝트 or 같은 프로젝트의 다른 기기가 같은 cellForm 을 공유할 경우
                for (iCellData in cellDataList) {
                    if (iCellData.cellFormId == cellForm.id) {
                        cellData = iCellData
                    }
                }

                // View 작업
                // 현재 보고서에 해당되는 SelectOptionData 중 CellFormId 일치하고 자동 입력이 아닌 항목을 RadioButton 으로 추가
                for (sod in sodList) {
                    if (sod.cellFormId == cellForm.id && !sod.isAuto) {
                        val radioButton = RadioButton(context).apply {
                            text = sod.content
                            id = sod.id!!.toInt()   // RadioButton ID 는 SelectOptionData 의 ID 로, 고유값임.
                            isChecked =
                                cellData?.content == sod.content    // 저장된 데이터와 일치하는 RadioButton 자동 체크

                            // 역시 데이터 날아가는 것 방지
                            if (dataSet[position] != null && dataSet[position]!!.content != "") {
                                isChecked = dataSet[position]!!.content == sod.content
                            }
                        }
                        radioGroup?.addView(radioButton)    // 현재 RadioGroup 에 추가
                    }
                }

                radioGroup?.setOnCheckedChangeListener { _, checkedId ->
                    val checkedContent = itemView?.findViewById<RadioButton?>(checkedId)?.text?.toString() ?: ""
                    if (dataSet[position] != null) {
                        dataSet[position]!!.content = checkedContent
                    } else {
                        dataSet[position] = CellData(
                            cellData?.id,
                            projectId,
                            machine.id,
                            cellForm.id,
                            checkedContent,
                            cell
                        )
                    }
                }
            }
            3 -> {  // 자동 입력
                // View Inflate
                itemView = LayoutInflater.from(context).inflate(R.layout.report_cell_data_edit_type3_rv_item, null)

                // Inflate 된 View 에서 각 요소를 추출
                val tvCellFormName: TextView? = itemView.tv_cell_form_name_t3
                val tvAutoFillData: TextView? = itemView.tv_cde_autoFill_data_t3

                // 현재 position 에 대한 cellForm 을 이용하여 데이터 매핑
                val cellForm = getItem(position)
                tvCellFormName?.text = cellForm.name

                val spinnerPosition =
                    sodList.find { (it.cellFormId == cellForm.id) && it.isAuto }?.content?.toInt()
                when (spinnerPosition) {
                    0 -> tvAutoFillData?.text = machine.computerizedNumber
                    1 -> tvAutoFillData?.text = machine.lineName.plus(machine.lineNumber)
                    2 -> tvAutoFillData?.text =
                        machine.manufacturingYear + "." + machine.manufacturingDate
                    3 -> tvAutoFillData?.text = machine.company
                    4 -> tvAutoFillData?.text = machine.machineIdInExcel
                    5 -> tvAutoFillData?.text = todayDateFormat
                }

                var cellDataId: Long? = null
                val cell = calculateCellLocation(cellForm.firstCell)

                // 데이터를 사용자가 입력하는 것이 아님.
                // DB UPDATE 를 위한 ID 설정만 해주면 됨 (사실 이것도 필요 없음)
                for (cellData in cellDataList) {
                    if (cellData.cellFormId == cellForm.id) {
                        cellDataId = cellData.id
                    }
                }

                dataSet[position] = CellData(
                    cellDataId,
                    projectId,
                    machine.id,
                    cellForm.id,
                    tvAutoFillData?.text.toString(),
                    cell
                )
            }
            else -> {
                itemView = View(context)    // null 만 안되게끔... 어차피 도달할 일 없음
            }
        }
        return itemView!!
    }

    override fun getCount(): Int {
        return cellFormList.size
    }

    override fun getItem(position: Int): CellForm {
        return cellFormList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 4
    }

    override fun getItemViewType(position: Int): Int {
        return cellFormList[position].type  // 1, 2, 3
    }

    internal fun setCellForms(cellForms: List<CellForm>) {
        this.cellFormList = cellForms
        notifyDataSetChanged()
    }

    internal fun setSodList(sodList: List<SelectOptionData>) {
        this.sodList = sodList
        notifyDataSetChanged()
    }

    internal fun setCellDataList(cellDataList: List<CellData>) {
        this.cellDataList = cellDataList
        notifyDataSetChanged()
    }

    internal fun setInterval(interval: Int) {
        this.interval = interval
        notifyDataSetChanged()
    }

    /**
     * [calculateCellLocation]
     * first_cell, interval, machineIdInExcel(연번)값을 이용하여 데이터의 실제 셀 위치를 계산하는 메소드
     */
    private fun calculateCellLocation(firstCell: String): String {
        val reNum = Regex("[^0-9]")    // 문자를 제거하기 위한 패턴
        val reEng = Regex("[^a-zA-Z]")  // 숫자, 특수문자를 제거하기 위한 패턴
        val number = reNum.replace(firstCell, "")
            .toInt() + (interval * (machine.machineIdInExcel.toInt() - 1))
        val alpha = reEng.replace(firstCell, "")

        return alpha + number.toString()
    }

    inner class CustomEditTextListener: TextWatcher {
        private var mPosition = 0

        fun updatePosition(position: Int) {
            mPosition = position
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (dataSet[mPosition] != null) {
                dataSet[mPosition]!!.content = s?.toString() ?: ""
            } else {
                dataSet[mPosition] = CellData(
                    null,
                    projectId,
                    machine.id,
                    getItem(mPosition).id,
                    s?.toString() ?: "",
                    ""
                )
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
}