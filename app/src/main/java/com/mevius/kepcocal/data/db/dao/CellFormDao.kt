package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.CellForm

@Dao
interface CellFormDao {
    @Query("SELECT * FROM CellForm")
    fun getAll(): LiveData<List<CellForm>>

    @Query("SELECT * FROM CellForm ORDER BY id DESC LIMIT 1")
    fun getLast(): LiveData<CellForm>

    @Query("SELECT * FROM CellForm WHERE report_id = :reportId")
    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>>

    @Query("SELECT * FROM CellForm WHERE id = :cellFormId")
    fun getCellFormWithId(cellFormId: Long): LiveData<CellForm>

    @Insert
    suspend fun insert(cellForm: CellForm)

    @Update
    suspend fun update(cellForm: CellForm)

    @Delete
    suspend fun delete(cellForm: CellForm)
}