package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ReportDao
import com.mevius.kepcocal.data.db.entity.Report
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao
): ReportRepository {
    override val allReports: LiveData<List<Report>> = reportDao.getAll()

    override suspend fun insert(report: Report) {
        reportDao.insert(report)
    }

    override suspend fun delete(report: Report) {
        reportDao.delete(report)
    }

    override fun getReportById(reportId: Long): LiveData<Report> {
        return reportDao.getReportById(reportId)
    }
}