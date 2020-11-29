package com.mevius.kepcocal.ui.project_detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.repository.MachineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailViewModel @ViewModelInject constructor(
    private val machineRepository: MachineRepository
) : ViewModel() {
    val allMachines: LiveData<List<Machine>> = machineRepository.allMachines

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>>{
        return machineRepository.getMachinesWithProjectId(projectId)
    }

    fun insert(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.insert(machine) }

    fun update(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.update(machine) }

    fun delete(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.delete(machine) }
}