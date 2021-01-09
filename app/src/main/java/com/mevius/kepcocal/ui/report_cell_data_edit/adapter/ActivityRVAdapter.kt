package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.ui.report_cell_form_edit.fragment.FragmentTypeOne
import kotlinx.android.synthetic.main.report_cell_data_edit_rv_item.view.*

class ActivityRVAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<ActivityRVAdapter.Holder>() {
    private var cellFormList = emptyList<CellForm>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.report_cell_data_edit_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return cellFormList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(cellFormList[position])
    }

    internal fun setCellForms(cellForms: List<CellForm>) {
        this.cellFormList = cellForms
        notifyDataSetChanged()
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvCellFormName: TextView? = itemView.tv_cell_form_name
        private var tvCellFormType: TextView? = itemView.tv_cell_form_type

        fun bind(cellForm: CellForm) {
            tvCellFormName?.text = cellForm.name
            when (cellForm.type) {
                1 -> {
                    tvCellFormType?.text = "직접 입력"
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.cde_inflate_parent_layout, FragmentTypeOne()).commit()
                }
                2 -> {
                    tvCellFormType?.text = "선택 입력"
                }
                3 -> {
                    tvCellFormType?.text = "자동 입력"
                }
                else -> {
                    tvCellFormType?.text = "미확인 타입"
                }
            }
        }
    }
}