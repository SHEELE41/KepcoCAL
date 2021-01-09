package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Project
import javax.inject.Inject

interface ProjectRepository {
    val allProjects: LiveData<List<Project>>

    val lastProject: LiveData<Project>

    fun getProjectWithId(projectId: Long): LiveData<Project>

    suspend fun insert(project: Project)

    suspend fun update(project: Project)

    suspend fun delete(project: Project)
}