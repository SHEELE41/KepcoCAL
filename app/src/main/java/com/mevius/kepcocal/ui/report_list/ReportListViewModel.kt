package com.mevius.kepcocal.ui.report_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.data.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportListViewModel @ViewModelInject constructor(
    private val reportRepository: ReportRepository
): ViewModel() {
    val allReports: LiveData<List<Report>> = reportRepository.allReports

    fun insertReport(report: Report) = viewModelScope.launch(Dispatchers.IO) {
        reportRepository.insert(report)
    }

    fun deleteReport(report: Report) = viewModelScope.launch(Dispatchers.IO) {
        reportRepository.delete(report)
    }
}