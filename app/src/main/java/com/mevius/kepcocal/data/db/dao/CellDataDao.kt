package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.CellData

@Dao
interface CellDataDao {
    @Query("SELECT * FROM CellData")
    fun getAll(): LiveData<List<CellData>>

    @Query("SELECT * FROM CellData WHERE project_id = :projectId")
    fun getCellDataByProjectId(projectId: Long): LiveData<List<CellData>>

    @Query("SELECT * FROM CellData WHERE machine_id = :machineId")
    fun getCellDataByMachineId(machineId: Long): LiveData<List<CellData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cellData: CellData)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(cellData: CellData)

    @Delete
    suspend fun delete(cellData: CellData)

    @Transaction
    suspend fun upsert(cellData: CellData) {
        insert(cellData)
        update(cellData)
    }
}