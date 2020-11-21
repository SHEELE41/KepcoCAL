package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Project
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val localDataSource: ProjectDao  // Room Dao for access Local Data Source
): ProjectRepository {
    override val allProjects: LiveData<List<Project>> = localDataSource.getAll()
    override val lastProject: LiveData<Project> = localDataSource.getLastProjectLive()

    override suspend fun insert(project: Project) {
        localDataSource.insert(project)
    }

    override suspend fun delete(project: Project) {
        localDataSource.delete(project)
    }
}