package com.mevius.kepcocal.ui.report_cell_form_list.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
            LayoutInflater.from(context)
                .inflate(R.layout.report_cell_form_list_rv_item, parent, false)
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
            tvCellFormType?.apply {
                when (cellForm.type) {
                    1 -> {
                        text = "직접 입력"
                        backgroundTintList = ColorStateList.valueOf(Color.rgb(0x75, 0x79, 0xE7))
                    }
                    2 -> {
                        text = "선택 입력"
                        backgroundTintList = ColorStateList.valueOf(Color.rgb(0x9a, 0xb3, 0xf5))
                    }
                    3 -> {
                        text = "자동 입력"
                        backgroundTintList = ColorStateList.valueOf(Color.rgb(0x72, 0x6a, 0xaf))
                    }
                    else -> {
                        text = "기타"
                        backgroundTintList = ColorStateList.valueOf(Color.rgb(0xb9, 0xff, 0xfc))
                    }
                }
            }
            tvFirstCell?.text = cellForm.firstCell

            itemView.setOnClickListener { itemClick(cellForm.id) }
            itemView.setOnLongClickListener { itemLongClick(cellForm) }
        }
    }
}