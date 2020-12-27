package com.mevius.kepcocal.ui.report_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Report
import kotlinx.android.synthetic.main.report_list_rv_item.view.*

class ReportRVAdapter(
    private val context: Context,
    private val itemClick: (Long?) -> Unit,
    private val itemLongClick: (Report) -> Boolean
) :
    RecyclerView.Adapter<ReportRVAdapter.Holder>() {
    private var reports = emptyList<Report>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.report_list_rv_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(reports[position])
    }

    internal fun setReports(reports: List<Report>) {
        this.reports = reports
        notifyDataSetChanged()
    }

    inner class Holder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private var tvReportTitle: TextView? = itemView.tv_report_title
        private var tvExtension: TextView? = itemView.tv_extension
        private var tvInterval: TextView? = itemView.tv_interval

        fun bind(report: Report) {
            tvReportTitle?.text = report.title
            tvExtension?.text = if (report.isXls) "xls" else "xlsx"
            tvInterval?.text = report.interval.toString()

            itemView.setOnClickListener { itemClick(report.id) }
            itemView.setOnLongClickListener { itemLongClick(report) }
        }
    }
}