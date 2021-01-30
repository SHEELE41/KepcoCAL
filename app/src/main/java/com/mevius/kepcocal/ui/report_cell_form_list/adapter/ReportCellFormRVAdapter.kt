package com.mevius.kepcocal.ui.report_cell_form_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellForm
import kotlinx.android.synthetic.main.report_cell_form_list_rv_item.view.*

class ReportCellFormRVAdapter(
    private val context: Context,
    private val itemClick: (Long?) -> Unit,
    private val itemLongClick: (CellForm) -> Boolean
) :
    RecyclerView.Adapter<ReportCellFormRVAdapter.Holder>() {
    private var cellForms = emptyList<CellForm>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.report_cell_form_list_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return cellForms.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(cellForms[position])
    }

    internal fun setCellForms(cellForms: List<CellForm>) {
        this.cellForms = cellForms
        notifyDataSetChanged()
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type
        private var tvFirstCell: TextView? = itemView.tv_first_cell

        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            tvCellFormType?.text = when(cellForm.type){
                1 -> "직접 입력"
                2 -> "선택 입력"
                3 -> "자동 입력"
                else -> "기타"
            }
            tvFirstCell?.text = cellForm.firstCell

            itemView.setOnClickListener { itemClick(cellForm.id) }
            itemView.setOnLongClickListener { itemLongClick(cellForm) }
        }
    }
}