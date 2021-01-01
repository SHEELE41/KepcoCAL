package com.mevius.kepcocal.ui.report_cell_form_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.repository.CellFormRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportCellFormListViewModel @ViewModelInject constructor(
    private val cellFormRepository: CellFormRepository
): ViewModel() {
    val allCellForms: LiveData<List<CellForm>> = cellFormRepository.allCellForms

    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>>{
        return cellFormRepository.getCellFormsWithReportId(reportId)
    }

    fun insertCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.insert(cellForm)
    }

    fun deleteCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.delete(cellForm)
    }
}