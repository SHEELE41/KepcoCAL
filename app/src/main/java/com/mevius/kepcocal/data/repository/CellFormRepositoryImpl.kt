package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.CellFormDao
import com.mevius.kepcocal.data.db.entity.CellForm
import javax.inject.Inject

class CellFormRepositoryImpl @Inject constructor(
    private val cellFormDao: CellFormDao
): CellFormRepository {
    override val allCellForms: LiveData<List<CellForm>> = cellFormDao.getAll()

    override val lastCellForm: LiveData<CellForm> = cellFormDao.getLast()

    override fun getCellFormsByReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormDao.getCellFormsByReportId(reportId)
    }

    override fun getCellFormById(cellFormId: Long): LiveData<CellForm> {
        return cellFormDao.getCellFormById(cellFormId)
    }

    override suspend fun insert(cellForm: CellForm) {
        cellFormDao.insert(cellForm)
    }

    override suspend fun delete(cellForm: CellForm) {
        cellFormDao.delete(cellForm)
    }
}