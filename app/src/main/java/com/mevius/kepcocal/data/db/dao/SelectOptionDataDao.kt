package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.SelectOptionData

@Dao
interface SelectOptionDataDao {
    @Query("SELECT * FROM SelectOptionData")
    fun getAll(): LiveData<List<SelectOptionData>>

    @Query("SELECT * FROM SelectOptionData WHERE cell_form_id = :cellFormId")
    fun getSelectOptionDataWithCellFormId(cellFormId: Long): LiveData<List<SelectOptionData>>

    @Insert
    suspend fun insert(selectOptionData: SelectOptionData)

    @Update
    suspend fun update(selectOptionData: SelectOptionData)

    @Delete
    suspend fun delete(selectOptionData: SelectOptionData)
}