package com.mevius.kepcocal.ui.report_cell_form_edit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import kotlinx.android.synthetic.main.report_cell_form_edit_type2_rv_item.view.*

class TypeTwoRVAdapter(
    private val context: Context,
    private val itemBtnClick: (SelectOptionData) -> Unit
) :
    RecyclerView.Adapter<TypeTwoRVAdapter.Holder>() {
    private var selectOptionDataList = emptyList<SelectOptionData>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.report_cell_form_edit_type2_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return selectOptionDataList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(selectOptionDataList[position])
    }

    internal fun setSelectOptionData(selectOptionDataList: List<SelectOptionData>) {
        this.selectOptionDataList = selectOptionDataList
        notifyDataSetChanged()
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvSelectOptionData: TextView? = itemView.type2_tv_select_option_data

        fun bind(selectOptionData: SelectOptionData) {
            tvSelectOptionData?.text = selectOptionData.content

            itemView.type2_btn_del.setOnClickListener { itemBtnClick(selectOptionData) }
        }
    }
}