package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.CellFormDao
import com.mevius.kepcocal.data.db.entity.CellForm
import javax.inject.Inject

class CellFormRepositoryImpl @Inject constructor(
    private val cellFormDao: CellFormDao
): CellFormRepository {
    override val allCellForms: LiveData<List<CellForm>> = cellFormDao.getAll()

    override fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormDao.getCellFormsWithReportId(reportId)
    }

    override fun getCellFormWithId(cellFormId: Long): LiveData<CellForm> {
        return cellFormDao.getCellFormWithId(cellFormId)
    }

    override suspend fun insert(cellForm: CellForm) {
        cellFormDao.insert(cellForm)
    }

    override suspend fun delete(cellForm: CellForm) {
        cellFormDao.delete(cellForm)
    }
}