package com.mevius.kepcocal.ui.report_list

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.data.repository.ReportRepository
import com.mevius.kepcocal.utils.FileHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportListViewModel @ViewModelInject constructor(
    private val reportRepository: ReportRepository,
    private val fileHelper: FileHelper
): ViewModel() {
    val allReports: LiveData<List<Report>> = reportRepository.allReports

    fun insertReportWithFileCopy(uri: Uri, reportTitleInput: String, reportIntervalInput: String) = viewModelScope.launch(Dispatchers.IO) {
        val isXls = fileHelper.saveFileAs(uri, reportTitleInput)
        val report = Report(
            null,
            reportTitleInput,
            reportIntervalInput.toInt(),
            isXls
        )
        reportRepository.insert(report)
    }

    fun deleteReportWithFile(report: Report) = viewModelScope.launch(Dispatchers.IO) {
        val fileName = if (report.isXls) report.title + ".xls" else report.title + ".xlsx"
        fileHelper.removeFile(fileName)
        reportRepository.delete(report)
    }
}