package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.CellFormDao
import com.mevius.kepcocal.data.db.entity.CellForm
import javax.inject.Inject

class CellFormRepositoryImpl @Inject constructor(
    private val cellFormDao: CellFormDao
): CellFormRepository {
    override val allCellForm: LiveData<List<CellForm>> = cellFormDao.getAll()

    override suspend fun insert(cellForm: CellForm) {
        cellFormDao.insert(cellForm)
    }

    override suspend fun delete(cellForm: CellForm) {
        cellFormDao.delete(cellForm)
    }
}