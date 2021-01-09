package com.mevius.kepcocal.ui.project_detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailViewModel @ViewModelInject constructor(
    private val machineRepository: MachineRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {
    val allMachines: LiveData<List<Machine>> = machineRepository.allMachines

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>>{
        return machineRepository.getMachinesWithProjectId(projectId)
    }

    fun getProjectWithId(projectId: Long): LiveData<Project>{
        return projectRepository.getProjectWithId(projectId)
    }

    fun insert(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.insert(machine) }

    fun update(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.update(machine) }

    fun update(project: Project) =
        viewModelScope.launch(Dispatchers.IO) { projectRepository.update(project) }

    fun delete(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.delete(machine) }
}