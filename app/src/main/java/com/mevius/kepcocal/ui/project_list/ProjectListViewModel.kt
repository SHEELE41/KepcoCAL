package com.mevius.kepcocal.ui.project_list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.utils.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectListViewModel @ViewModelInject constructor(
    private val projectRepository: ProjectRepository,
    private val machineRepository: MachineRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    val allProjects: LiveData<List<Project>> = projectRepository.allProjects

    fun insertProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.insert(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.delete(project)
    }

    fun insertMachinesFromExcel(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        if (networkHelper.isNetworkConnected()){
            machineRepository.insertMachinesFromExcel(this, project)
        } else {
            Log.d("####################","Error")
        }
    }
}