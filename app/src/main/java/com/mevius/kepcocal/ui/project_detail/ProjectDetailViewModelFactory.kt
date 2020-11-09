package com.mevius.kepcocal.ui.project_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mevius.kepcocal.data.repository.MachineRepository

class ProjectDetailViewModelFactory(
    private val machineRepository: MachineRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectDetailViewModel(machineRepository) as T
    }
}