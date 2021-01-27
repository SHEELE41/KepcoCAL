package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.CellData

interface CellDataRepository {
    val allCellData: LiveData<List<CellData>>

    fun getCellDataWithProjectId(projectId: Long): LiveData<List<CellData>>

    fun getCellDataWithMachineId(machineId: Long): LiveData<List<CellData>>

    suspend fun insert(cellData: CellData)

    suspend fun update(cellData: CellData)

    suspend fun upsert(cellData: CellData)

    suspend fun delete(cellData: CellData)
}