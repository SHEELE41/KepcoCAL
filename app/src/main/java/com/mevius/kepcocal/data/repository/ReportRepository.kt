package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.Report

interface ReportRepository {
    val allReports: LiveData<List<Report>>

    suspend fun insert(report: Report)

    suspend fun delete(report: Report)

    fun getReportById(reportId: Long): LiveData<Report>
}