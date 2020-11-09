package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Project

class ProjectRepository constructor(
    private val localDataSource: ProjectDao  // Room Dao for access Local Data Source
) {
    // Singleton Pattern
    companion object {
        @Volatile
        private var instance: ProjectRepository? = null

        fun getInstance(projectDao: ProjectDao): ProjectRepository {
            return instance ?: synchronized(this) {
                instance ?: ProjectRepository(projectDao).also { instance = it }
            }
        }
    }

    val allProjects: LiveData<List<Project>> = localDataSource.getAll()
    val lastProject: LiveData<Project> = localDataSource.getLastProjectLive()

    suspend fun insert(project: Project) {
        localDataSource.insert(project)
    }

    suspend fun delete(project: Project) {
        localDataSource.delete(project)
    }
}