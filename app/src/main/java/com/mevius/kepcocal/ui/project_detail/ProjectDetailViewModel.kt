package com.mevius.kepcocal.ui.project_detail

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mevius.kepcocal.GlobalApplication
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.data.repository.CellDataRepository
import com.mevius.kepcocal.data.repository.MachineRepository
import com.mevius.kepcocal.data.repository.ProjectRepository
import com.mevius.kepcocal.data.repository.ReportRepository
import com.mevius.kepcocal.utils.ExcelManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProjectDetailViewModel @ViewModelInject constructor(
    application: Application,
    private val machineRepository: MachineRepository,
    private val projectRepository: ProjectRepository,
    private val reportRepository: ReportRepository,
    private val cellDataRepository: CellDataRepository
) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val allMachines: LiveData<List<Machine>> = machineRepository.allMachines
    val allReports: LiveData<List<Report>> = reportRepository.allReports

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>>{
        return machineRepository.getMachinesWithProjectId(projectId)
    }

    fun getCellDataWithProjectId(projectId: Long): LiveData<List<CellData>>{
        return cellDataRepository.getCellDataWithProjectId(projectId)
    }

    fun getProjectWithId(projectId: Long): LiveData<Project>{
        return projectRepository.getProjectWithId(projectId)
    }

    fun getReportWithId(reportId: Long): LiveData<Report>{
        return reportRepository.getReportWithId(reportId)
    }

    fun writeReportExcel(cellDataList: List<CellData>, report: Report?) =
        viewModelScope.launch(Dispatchers.IO) {
            val mOutputDir = context.getExternalFilesDir(null)
            when (report?.isXls) {
                true -> {
                    Log.d("xls Case", report.title)
                    ExcelManager(
                        Uri.fromFile(
                            File(
                                mOutputDir,
                                "/${report.title}.xls"
                            )
                        )
                    ).writeReport(
                        cellDataList
                    )
                }
                false -> {
                    Log.d("xlsx Case", report.title)
                    ExcelManager(
                        Uri.fromFile(
                            File(
                                mOutputDir,
                                "/${report.title}.xlsx"
                            )
                        )
                    ).writeReport(
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