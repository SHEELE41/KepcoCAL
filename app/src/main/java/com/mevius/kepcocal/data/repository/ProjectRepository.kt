package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Project

class ProjectRepository constructor(
    private val localDataSource: ProjectDao  //
){
    val allProjects: LiveData<List<Project>> = localDataSource.getAll()

    suspend fun insert(project: Project){
        localDataSource.insert(project)
    }

    suspend fun delete(project: Project){
        localDataSource.delete(project)
    }
}