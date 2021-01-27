package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import kotlin.collections.HashMap

class ReportCellDataEditRVAdapter(
    private val context: Context,
    private val machine: Machine,
    private val dataSet: HashMap<Int, CellData>,
    private val machineId: Long,
    private val projectId: Long,
    private val interval: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

            for (cellData in cellDataList) {
                if (cellData.cellFormId == cellForm.id) {
                    initContent = cellData.content
                    cellDataId = cellData.id
                    editText?.setText(cellData.content)
                }
            }

            val cell = calculateCellLocation(cellForm.firstCell)

            dataSet[this.adapterPosition] = CellData(
                cellDataId,
                projectId,
                cellForm.id,
                initContent,
                cell,
                machineId.toInt()
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

            val radioButtonList = mutableListOf<RadioButton>()

            for (sod in sodList) {
                if (sod.cellFormId == cellForm.id && !sod.isAuto) {
                    val radioButton = RadioButton(context).apply {
                        text = sod.content
                        id = sod.id!!.toInt()
                    }
                    radioGroup?.addView(radioButton)
                    radioButtonList.add(radioButton)
                }
            }

            var cellDataId: Long? = null

            for (cellData in cellDataList) {
                if (cellData.cellFormId == cellForm.id) {
                    cellDataId = cellData.id
                    for (radioButton in radioButtonList) {
                        if (cellData.content == radioButton.text) {
                            radioButton.isChecked = true
                        }
                    }
                }
            }

            val cell = calculateCellLocation(cellForm.firstCell)

            radioGroup?.setOnCheckedChangeListener { _, checkedId ->
                val checkedContent = itemView.findViewById<RadioButton>(checkedId).text.toString()
                if (dataSet[this.adapterPosition] != null) {
                    dataSet[this.adapterPosition]!!.content = checkedContent
                } else {
                    dataSet[this.adapterPosition] = CellData(
                        cellDataId,
                        projectId,
                        cellForm.id,
                        checkedContent,
                        cell,
                        machineId.toInt()
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

            // TODO 나중에 DB 최적화 꼭 필요
            val spinnerPosition =
                sodList.find { (it.cellFormId == cellForm.id) && it.isAuto }?.content?.toInt()
            Log.d("########################################", spinnerPosition.toString())
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

            for (cellData in cellDataList) {
                if (cellData.cellFormId == cellForm.id) {
                    cellDataId = cellData.id
                }
            }

            val cell = calculateCellLocation(cellForm.firstCell)

            dataSet[this.adapterPosition] = CellData(
                cellDataId,
                projectId,
                cellForm.id,
                tvAutoFillData?.text.toString(),
                cell,
                machineId.toInt()
            )
        }
    }

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
                    null,
                    s.toString(),
                    "",
                    machineId.toInt()
                )
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
}