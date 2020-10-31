package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mevius.kepcocal.data.db.entity.Project

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project")
    fun getAll(): LiveData<List<Project>>

    @Insert
    suspend fun insert(project: Project)

    @Delete
    suspend fun delete(project: Project)
}