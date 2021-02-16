package com.mevius.kepcocal.ui.report_cell_data_edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.db.entity.Report
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.data.repository.CellDataRepository
import com.mevius.kepcocal.data.repository.CellFormRepository
import com.mevius.kepcocal.data.repository.ReportRepository
import com.mevius.kepcocal.data.repository.SelectOptionDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportCellDataEditViewModel @ViewModelInject constructor(
    private val reportRepository: ReportRepository,
    private val cellFormRepository: CellFormRepository,
    private val selectOptionDataRepository: SelectOptionDataRepository,
    private val cellDataRepository: CellDataRepository
) : ViewModel() {
    fun getCellFormsByReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormRepository.getCellFormsByReportId(reportId)
    }

    fun getReportById(reportId: Long): LiveData<Report> {
        return reportRepository.getReportById(reportId)
    }

    private fun insertCellData(cellData: CellData) = viewModelScope.launch(Dispatchers.IO) {
        cellDataRepository.insert(cellData)
    }

    private fun deleteCellData(cellData: CellData) = viewModelScope.launch(Dispatchers.IO) {
        cellDataRepository.delete(cellData)
    }

    fun updateTransaction(cellData: CellData) = viewModelScope.launch(Dispatchers.IO) {
        deleteCellData(cellData).join()     // SelectOptionData CASCADE
        insertCellData(cellData).join()
    }

    fun getCellDataByMachineId(machineId: Long): LiveData<List<CellData>> {
        return cellDataRepository.getCellDataByMachineId(machineId)
    }

    fun getSelectOptionDataByReportId(reportId: Long): LiveData<List<SelectOptionData>> {
        return selectOptionDataRepository.getSelectOptionDataByReportId(reportId)
    }
}