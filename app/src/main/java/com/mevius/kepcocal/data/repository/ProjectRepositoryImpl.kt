package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Project
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao  // Room Dao for access Local Data Source
): ProjectRepository {
    override val allProjects: LiveData<List<Project>> = projectDao.getAll()
    override val lastProject: LiveData<Project> = projectDao.getLastProjectLive()
    override fun getProjectWithId(projectId: Long): LiveData<Project> {
        return projectDao.getProjectWithId(projectId)
    }

    override suspend fun insert(project: Project) {
        projectDao.insert(project)
    }

    override suspend fun update(project: Project) {
        projectDao.update(project)
    }

    override suspend fun delete(project: Project) {
        projectDao.delete(project)
    }
}