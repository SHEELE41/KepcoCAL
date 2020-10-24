package com.mevius.kepcocal.view.project_list.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mevius.kepcocal.R

class ProjectListViewAdapter (private val context: Context, private val itemDataList : ArrayList<ProjectListViewItemData>) : BaseAdapter() {
    override fun getCount(): Int {
        return itemDataList.size
    }

    override fun getItem(position: Int): Any {
        return itemDataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.project_list_listview_item, parent, false)
        val tvProjectName = view.findViewById<TextView>(R.id.tv_project_name)
        val tvDate = view.findViewById<TextView>(R.id.tv_date)

        val itemData = itemDataList[position]

        tvProjectName.text = itemData.projectName
        tvDate.text = itemData.modifiedDate

        return view
    }
}