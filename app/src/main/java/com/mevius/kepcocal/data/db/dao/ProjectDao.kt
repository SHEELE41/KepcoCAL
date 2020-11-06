package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.db.entity.ProjectWithMachines

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project")
    fun getAll(): LiveData<List<Project>>

    @Query("SELECT * FROM Project ORDER BY id DESC LIMIT 1")
    fun getLastProjectLive(): LiveData<Project>

    @Insert
    suspend fun insert(project: Project)

    @Delete
    suspend fun delete(project: Project)

    @Transaction
    @Query("SELECT * FROM Project")
    suspend fun getProjectWithMachines(): List<ProjectWithMachines>
}