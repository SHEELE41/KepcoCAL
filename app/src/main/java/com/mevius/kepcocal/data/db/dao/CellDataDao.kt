package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.CellData

@Dao
interface CellDataDao {
    @Query("SELECT * FROM CellData")
    fun getAll(): LiveData<List<CellData>>

    @Query("SELECT * FROM CellData WHERE project_id = :projectId")
    fun getCellDataWithProjectId(projectId: Long): LiveData<List<CellData>>

    @Insert
    suspend fun insert(cellData: CellData)

    @Update
    suspend fun update(cellData: CellData)

    @Delete
    suspend fun delete(cellData: CellData)
}