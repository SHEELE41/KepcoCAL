package com.mevius.kepcocal.ui.project_list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.utils.Event
import com.mevius.kepcocal.utils.NetworkHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectListViewModel @ViewModelInject constructor(
    private val projectRepository: ProjectRepository,
    private val machineRepository: MachineRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    // LiveData to handle error Toast
    private val _showErrorToast = MutableLiveData<Event<String>>()
    val showErrorToast: LiveData<Event<String>> = _showErrorToast

    // 예외 발생 시 실행될 메소드
    private fun onErrorOccur(content: String) {
        _showErrorToast.value = Event(content)
    }

    val allProjects: LiveData<List<Project>> = projectRepository.allProjects

    fun insertProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.insert(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        projectRepository.delete(project)
    }

    fun insertMachinesFromExcel(project: Project) {
        // 예외 처리를 위한 Handler
        val handler = CoroutineExceptionHandler { _, exception ->
            deleteProject(project)  // 잘못 추가된 프로젝트 삭제
            onErrorOccur("작업 중 오류가 발생하였습니다.")
            Log.v("ProjectListViewModel.kt", exception.toString())
        }

        // Toast 가 UI 작업이라 기본적으로 Main Thread 에서 실행시켜 주어야 함
        viewModelScope.launch(Dispatchers.Main + handler) {
            if (networkHelper.isNetworkConnected()) {
                withContext(Dispatchers.IO) {
                    machineRepository.insertMachinesFromExcel(this, project)
                }
            } else {    // 네트워크 연결 안 되어있을 경우
                deleteProject(project)  // 잘못 추가된 프로젝트 삭제
                onErrorOccur("네트워크 연결을 확인해주세요.")
            }
        }
    }
}