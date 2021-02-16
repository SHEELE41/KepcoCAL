package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.Machine

@Dao
interface MachineDao {
    @Query("SELECT * FROM Machine")
    fun getAll(): LiveData<List<Machine>>

    @Query("SELECT * FROM Machine WHERE project_id = :projectId")
    fun getMachinesByProjectId(projectId: Long): LiveData<List<Machine>>

    @Insert
    suspend fun insert(machine: Machine)

    @Update
    suspend fun update(machine: Machine)

    @Delete
    suspend fun delete(machine: Machine)
}