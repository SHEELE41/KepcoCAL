package com.mevius.kepcocal.ui.report_cell_data_edit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import kotlinx.android.synthetic.main.report_cell_data_edit_type2_rv_item.view.*

class TypeTwoFragmentRVAdapter(
    private val context: Context,
    private val selectOptionDataList: MutableList<SelectOptionData>
) :
    RecyclerView.Adapter<TypeTwoFragmentRVAdapter.Holder>() {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.report_cell_data_edit_type2_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return selectOptionDataList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(selectOptionDataList[position])
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var rbTypeTwoItemSOD: RadioButton? = itemView.radioButton_in_type2_item

        fun bind(selectOptionData: SelectOptionData) {
            rbTypeTwoItemSOD?.text = selectOptionData.content
        }
    }
}