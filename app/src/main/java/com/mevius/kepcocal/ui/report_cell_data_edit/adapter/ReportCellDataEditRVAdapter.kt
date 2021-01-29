package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

class ReportCellDataEditRVAdapter(
    private val context: Context,
    private val machine: Machine,
    private val projectId: Long,
    private val dataSet: HashMap<Int, CellData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var interval = 0
    private var cellFormList = emptyList<CellForm>()
    private var sodList = emptyList<SelectOptionData>()
    private var cellDataList = emptyList<CellData>()
    private val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return cellFormList[position].type  // ViewType = 1, 2, 3 (직접 입력, 선택 입력, 자동 입력)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder?
        when (viewType) {
            1 -> {
                val itemView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.report_cell_data_edit_type1_rv_item, parent, false)
                holder = Holder1(itemView, CustomEditTextListener())
            }
            2 -> {
                val itemView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.report_cell_data_edit_type2_rv_item, parent, false)
                holder = Holder2(itemView)
            }
            3 -> {
                val itemView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.report_cell_data_edit_type3_rv_item, parent, false)
                holder = Holder3(itemView)
            }
            else -> holder = null   // 형식상. null 이 될 일은 없음
        }
        return holder!!
    }

    override fun getItemCount(): Int {
        return cellFormList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val viewHolder0 = holder as Holder1
                viewHolder0.mCustomEditTextListener.updatePosition(holder.adapterPosition)
                viewHolder0.bind(cellFormList[position])
            }
            2 -> {
                val viewHolder2 = holder as Holder2
                viewHolder2.bind(cellFormList[position])
            }
            3 -> {
                val viewHolder3 = holder as Holder3
                viewHolder3.bind(cellFormList[position])
            }
        }
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

    // Type1. 직접 입력
    inner class Holder1(
        itemView: View,
        customEditTextListener: CustomEditTextListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name_t1
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type_t1
        private var editText: AutoCompleteTextView? = itemView.input_cell_data_content_t1
        val mCustomEditTextListener = customEditTextListener

        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = "직접 입력"
            editText?.addTextChangedListener(mCustomEditTextListener)

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
                    editText?.setText(cellData.content)
                }
            }

            // bind 될 때 기본적으로 CellData 객체를 position 에 생성
            dataSet[this.adapterPosition] = CellData(
                cellDataId,
                projectId,
                machine.id,
                cellForm.id,
                initContent,
                cell
            )
        }
    }

    // Type2. 선택 입력
    inner class Holder2(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name_t2
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type_t2

        fun bind(cellForm: CellForm) {
            val radioGroup: RadioGroup? = itemView.radio_group_inner_item_type2
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = "선택 입력"

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
                    }
                    radioGroup?.addView(radioButton)    // 현재 RadioGroup 에 추가
                }
            }

            radioGroup?.setOnCheckedChangeListener { _, checkedId ->
                val checkedContent = itemView.findViewById<RadioButton?>(checkedId)?.text?.toString() ?: ""
                if (dataSet[this.adapterPosition] != null) {
                    dataSet[this.adapterPosition]!!.content = checkedContent
                } else {
                    dataSet[this.adapterPosition] = CellData(
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
    }

    // Type3. 자동 입력
    inner class Holder3(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name_t3
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type_t3
        private var tvAutoFillData: TextView? = itemView.tv_cde_autoFill_data_t3

        @SuppressLint("SetTextI18n")
        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = "자동 입력"

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

            dataSet[this.adapterPosition] = CellData(
                cellDataId,
                projectId,
                machine.id,
                cellForm.id,
                tvAutoFillData?.text.toString(),
                cell
            )
        }
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

    inner class CustomEditTextListener : TextWatcher {
        private var mPosition = 0

        fun updatePosition(position: Int) {
            mPosition = position
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (dataSet[mPosition] != null) {
                dataSet[mPosition]!!.content = s.toString()
            } else {
                dataSet[mPosition] = CellData(
                    null,
                    projectId,
                    machine.id,
                    null,
                    s.toString(),
                    ""
                )
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
}