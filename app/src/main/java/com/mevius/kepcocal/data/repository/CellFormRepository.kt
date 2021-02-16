package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.CellForm

interface CellFormRepository {
    val allCellForms: LiveData<List<CellForm>>

    val lastCellForm: LiveData<CellForm>

    fun getCellFormsByReportId(reportId: Long): LiveData<List<CellForm>>

    fun getCellFormById(cellFormId: Long): LiveData<CellForm>

    suspend fun insert(cellForm: CellForm)

    suspend fun delete(cellForm: CellForm)
}