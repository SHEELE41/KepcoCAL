package com.mevius.kepcocal.ui.project_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Project
import kotlinx.android.synthetic.main.project_list_rv_item.view.*

class ProjectRVAdapter(
    private val context: Context,
    private val itemClick: (Long?) -> Unit,
    private val itemLongClick: (Project) -> Boolean
) :
    RecyclerView.Adapter<ProjectRVAdapter.Holder>() {
    private var projects = emptyList<Project>()

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
        return projects.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(projects[position])
    }

    internal fun setProjects(projects: List<Project>) {
        this.projects = projects
        notifyDataSetChanged()
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvProjectName: TextView? = itemView.tv_project_name
        private var tvDate: TextView? = itemView.tv_date

        fun bind(project: Project) {
            tvProjectName?.text = project.projectName
            tvDate?.text = project.modifiedDate

            itemView.setOnClickListener { itemClick(project.id) }
            itemView.setOnLongClickListener { itemLongClick(project) }
        }
    }
}