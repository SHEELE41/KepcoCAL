package com.mevius.kepcocal.ui.project_detail

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
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
import com.mevius.kepcocal.utils.ExcelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailViewModel @ViewModelInject constructor(
    private val machineRepository: MachineRepository,
    private val projectRepository: ProjectRepository,
    private val reportRepository: ReportRepository,
    private val cellDataRepository: CellDataRepository,
    private val excelHelper: ExcelHelper
) : ViewModel() {
    val allReports: LiveData<List<Report>> = reportRepository.allReports

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>> {
        return machineRepository.getMachinesWithProjectId(projectId)
    }

    fun getCellDataWithProjectId(projectId: Long): LiveData<List<CellData>> {
        return cellDataRepository.getCellDataWithProjectId(projectId)
    }

    fun getProjectWithId(projectId: Long): LiveData<Project> {
        return projectRepository.getProjectWithId(projectId)
    }

    fun getReportWithId(reportId: Long): LiveData<Report> {
        return reportRepository.getReportWithId(reportId)
    }

    fun writeReportExcel(cellDataList: List<CellData>, report: Report?, outputFileName: String) =
        viewModelScope.launch(Dispatchers.IO) {
            when (report?.isXls) {
                true -> {
                    Log.d("xls Case", report.title)
                    excelHelper.writeReport(
                        "/${report.title}.xls",
                        "/$outputFileName.xls",
                        cellDataList
                    )
                }
                false -> {
                    Log.d("xlsx Case", report.title)
                    excelHelper.writeReport(
                        "/${report.title}.xlsx",
                        "/$outputFileName.xlsx",
                        cellDataList
                    )
                }
                else -> {
                    // TODO EventWrapper
                    // Toast.makeText(context, "유효하지 않은 보고서입니다.", Toast.LENGTH_SHORT).show()
                }
            }
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