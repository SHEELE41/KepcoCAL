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
): ViewModel() {
    val allSelectOptionData = selectOptionDataRepository.allSelectOptionData
    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormRepository.getCellFormsWithReportId(reportId)
    }

    fun getReportWithId(reportId: Long): LiveData<Report> {
        return reportRepository.getReportWithId(reportId)
    }

    fun insertCellData(cellData: CellData) = viewModelScope.launch(Dispatchers.IO) {
        cellDataRepository.insert(cellData)
    }

    fun getSelectOptionDataWithCellFormIdAndAutoFlag(cellFormId: Long, isAuto: Boolean): LiveData<List<SelectOptionData>> {
        return selectOptionDataRepository.getSelectOptionDataWithCellFormIdAndAutoFlag(cellFormId, isAuto)
    }

    fun getCellFormWithId(cellFormId: Long): LiveData<CellForm> {
        return cellFormRepository.getCellFormWithId(cellFormId)
    }
}