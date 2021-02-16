package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Report

@Dao
interface ReportDao {
    @Query("SELECT * FROM Report")
    fun getAll(): LiveData<List<Report>>

    @Query("SELECT * FROM Report WHERE id = :reportId")
    fun getReportById(reportId: Long): LiveData<Report>

    @Insert
    suspend fun insert(report: Report)

    @Delete
    suspend fun delete(report: Report)
}