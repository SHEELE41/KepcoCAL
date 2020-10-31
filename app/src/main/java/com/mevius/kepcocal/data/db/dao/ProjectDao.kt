package com.mevius.kepcocal.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mevius.kepcocal.data.db.entity.Project

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project")
    fun getAll(): List<Project>

    @Insert
    fun insertAll(vararg projects: Project)

    @Delete
    fun delete(project: Project)
}