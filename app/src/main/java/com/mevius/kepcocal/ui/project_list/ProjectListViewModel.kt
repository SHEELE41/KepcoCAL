package com.mevius.kepcocal.ui.project_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.AppDatabase
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectListViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ProjectRepository
    val allProjects: LiveData<List<Project>>

    init {
        val projectDao = AppDatabase.getDatabase(application, viewModelScope).projectDao()
        repository = ProjectRepository(projectDao)
        allProjects = repository.allProjects
    }

    fun insert(project: Project) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(project)
    }

    fun delete(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(project)
    }
}