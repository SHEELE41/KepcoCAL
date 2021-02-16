package com.mevius.kepcocal.ui.project_detail

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.data.repository.CellDataRepository
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.data.repository.ReportRepository
import com.mevius.kepcocal.utils.Event
import com.mevius.kepcocal.utils.ExcelHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectDetailViewModel @ViewModelInject constructor(
    private val machineRepository: MachineRepository,
    private val projectRepository: ProjectRepository,
    private val reportRepository: ReportRepository,
    private val cellDataRepository: CellDataRepository,
    private val excelHelper: ExcelHelper
) : ViewModel() {
    // LiveData to handle Toast
    private val _showWorkStatusToast = MutableLiveData<Event<String>>()
    val showWorkStatusToast: LiveData<Event<String>> = _showWorkStatusToast

    // 보고서 작업 중단 / 완료 시 실행될 메소드
    private fun onWorkStatusChanged(content: String) {
        _showWorkStatusToast.value = Event(content)
    }

    val allReports: LiveData<List<Report>> = reportRepository.allReports

    fun updateMachine(machine: Machine) =
        viewModelScope.launch(Dispatchers.IO) { machineRepository.update(machine) }

    fun updateProject(project: Project) =
        viewModelScope.launch(Dispatchers.IO) { projectRepository.update(project) }

    fun getMachinesByProjectId(projectId: Long): LiveData<List<Machine>> {
        return machineRepository.getMachinesByProjectId(projectId)
    }

    fun getCellDataByProjectId(projectId: Long): LiveData<List<CellData>> {
        return cellDataRepository.getCellDataByProjectId(projectId)
    }

    fun getProjectById(projectId: Long): LiveData<Project> {
        return projectRepository.getProjectById(projectId)
    }

    fun getReportById(reportId: Long): LiveData<Report> {
        return reportRepository.getReportById(reportId)
    }

    fun writeReportExcel(
        cellDataList: List<CellData>,
        inputFilePath: String,
        outputFilePath: String
    ) = viewModelScope.launch(Dispatchers.Main) {
        // 예외 처리를 위한 Handler
        val handler = CoroutineExceptionHandler { _, exception ->
            onWorkStatusChanged("작업 중 오류가 발생하였습니다.")
            Log.v("ProjectDetailViewModel.kt", exception.toString())
        }

        withContext(Dispatchers.IO + handler) {
            excelHelper.writeReport(
                inputFilePath,
                outputFilePath,
                cellDataList
            )
        }

        onWorkStatusChanged("보고서가 저장되었습니다.")
    }
}