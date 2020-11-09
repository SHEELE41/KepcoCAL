package com.mevius.kepcocal.ui.project_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mevius.kepcocal.data.repository.ProjectRepository

class ProjectListViewModelFactory(
    private val projectRepository: ProjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectListViewModel(projectRepository) as T
    }
}