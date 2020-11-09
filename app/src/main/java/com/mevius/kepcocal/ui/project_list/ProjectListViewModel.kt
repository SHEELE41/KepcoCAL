package com.mevius.kepcocal.ui.project_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectListViewModel constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    val allProjects: LiveData<List<Project>> = projectRepository.allProjects
    val lastProject: LiveData<Project> = projectRepository.lastProject

    fun insert(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.insert(project)
    }

    fun delete(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.delete(project)
    }
}