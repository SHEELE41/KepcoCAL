package com.mevius.kepcocal.ui.project_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectListViewModel constructor(
    private val projectRepository: ProjectRepository,
    private val machineRepository: MachineRepository
) : ViewModel() {
    val allProjects: LiveData<List<Project>> = projectRepository.allProjects
    val lastProject: LiveData<Project> = projectRepository.lastProject

    fun insertProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.insert(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.delete(project)
    }

    fun insertMachinesFromExcel(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        machineRepository.insertMachinesFromExcel(this, project)
    }
}