package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.CellForm

interface CellFormRepository {
    val allCellForms: LiveData<List<CellForm>>

    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>>

    fun getCellFormWithId(cellFormId: Long): LiveData<CellForm>

    suspend fun insert(cellForm: CellForm)

    suspend fun delete(cellForm: CellForm)
}