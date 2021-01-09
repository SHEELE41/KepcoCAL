package com.mevius.kepcocal.ui.report_cell_data_edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.data.repository.CellDataRepository
import com.mevius.kepcocal.data.repository.CellFormRepository
import com.mevius.kepcocal.data.repository.SelectOptionDataRepository

class ReportCellDataEditViewModel @ViewModelInject constructor(
    private val cellFormRepository: CellFormRepository,
    private val selectOptionDataRepository: SelectOptionDataRepository,
    private val cellDataRepository: CellDataRepository
){
    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormRepository.getCellFormsWithReportId(reportId)
    }

    fun getSelectOptionDataWithCellFormIdAndAutoFlag(cellFormId: Long, isAuto: Boolean): LiveData<List<SelectOptionData>> {
        return selectOptionDataRepository.getSelectOptionDataWithCellFormIdAndAutoFlag(cellFormId, isAuto)
    }

    fun getCellFormWithId(cellFormId: Long): LiveData<CellForm> {
        return cellFormRepository.getCellFormWithId(cellFormId)
    }
}