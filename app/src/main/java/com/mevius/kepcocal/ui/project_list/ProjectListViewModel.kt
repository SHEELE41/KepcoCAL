package com.mevius.kepcocal.ui.project_list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.utils.Event
import com.mevius.kepcocal.utils.NetworkHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ArithmeticException
import java.lang.Exception

class ProjectListViewModel @ViewModelInject constructor(
    private val projectRepository: ProjectRepository,
    private val machineRepository: MachineRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _showErrorToast = MutableLiveData<Event<String>>()
    val showErrorToast = _showErrorToast
//    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
//        onErrorOccur(exception.toString())
//    }

    private fun onErrorOccur(e: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _showErrorToast.value = Event(e)
        }
    }

    val allProjects: LiveData<List<Project>> = projectRepository.allProjects

    fun insertProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.insert(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.delete(project)
    }

    fun insertMachinesFromExcel(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        if (networkHelper.isNetworkConnected()) {
            machineRepository.insertMachinesFromExcel(this, project)
        } else {
            onErrorOccur("네트워크 오류가 발생했습니다.")
        }
    }
}