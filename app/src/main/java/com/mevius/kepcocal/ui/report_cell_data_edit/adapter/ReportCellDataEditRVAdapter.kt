package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
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
    private val machine: Machine
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cellFormList = emptyList<CellForm>()
    private var sodList = emptyList<SelectOptionData>()
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
                holder = Holder1(itemView)
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

    // Type1. 직접 입력
    inner class Holder1(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name_t1
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type_t1

        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = "직접 입력"
        }
    }

    // Type2. 선택 입력
    inner class Holder2(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name_t2
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type_t2
        private var radioGroup: RadioGroup? = itemView.radio_group_inner_item_type2

        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = "선택 입력"
            for (sod in sodList) {
                if (sod.cellFormId == cellForm.id && !sod.isAuto){
                    val radioButton = RadioButton(context).apply {
                        text = sod.content
                        id = sod.id!!.toInt()
                    }
                    radioGroup?.addView(radioButton)
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
        }
    }
}