package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.CellDataDao
import com.mevius.kepcocal.data.db.entity.CellData
import javax.inject.Inject

class CellDataRepositoryImpl @Inject constructor(
    private val cellDataDao: CellDataDao
): CellDataRepository {
    override val allCellData: LiveData<List<CellData>> = cellDataDao.getAll()

    override fun getCellDataByProjectId(projectId: Long): LiveData<List<CellData>> {
        return cellDataDao.getCellDataByProjectId(projectId)
    }

    override fun getCellDataByMachineId(machineId: Long): LiveData<List<CellData>> {
        return cellDataDao.getCellDataByMachineId(machineId)
    }

    override suspend fun insert(cellData: CellData) {
        cellDataDao.insert(cellData)
    }

    override suspend fun update(cellData: CellData) {
        cellDataDao.update(cellData)
    }

    override suspend fun upsert(cellData: CellData) {
        cellDataDao.upsert(cellData)
    }

    override suspend fun delete(cellData: CellData) {
        cellDataDao.delete(cellData)
    }
}