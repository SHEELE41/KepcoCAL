package com.mevius.kepcocal.ui.project_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.utils.NetworkHelper

class ProjectListViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val machineRepository: MachineRepository,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectListViewModel(projectRepository, machineRepository, networkHelper) as T
    }
}