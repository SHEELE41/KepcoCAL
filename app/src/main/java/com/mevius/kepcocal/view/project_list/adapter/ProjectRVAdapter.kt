package com.mevius.kepcocal.view.project_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import kotlinx.android.synthetic.main.project_list_rv_item.view.*

class ProjectRVAdapter(
    private val context: Context,
    private val itemDataList: ArrayList<ProjectRVItemData>,
    private val itemClick: (ProjectRVItemData) -> Unit,
    private val itemLongClick: (ProjectRVItemData) -> Boolean
) :
    RecyclerView.Adapter<ProjectRVAdapter.Holder>() {
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.project_list_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return itemDataList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemDataList[position], context)
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        var tvProjectName: TextView? = itemView.tv_project_name
        var tvDate: TextView? = itemView.tv_date

        fun bind(projectRVItemData: ProjectRVItemData, context: Context) {
            tvProjectName?.text = projectRVItemData.projectName
            tvDate?.text = projectRVItemData.modifiedDate

            itemView.setOnClickListener { itemClick(projectRVItemData) }
            itemView.setOnLongClickListener { itemLongClick(projectRVItemData) }
        }
    }
}