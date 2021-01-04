package com.mevius.kepcocal.ui.report_cell_form_edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevius.kepcocal.data.db.entity.CellForm
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.data.repository.CellFormRepository
import com.mevius.kepcocal.data.repository.SelectOptionDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportCellFormEditViewModel @ViewModelInject constructor(
    private val cellFormRepository: CellFormRepository,
    private val selectOptionDataRepository: SelectOptionDataRepository
): ViewModel() {
    var reportId = 0L
    var cellFormId = 0L
    var selectOptionDataCacheList = ArrayList<SelectOptionData>()

    val allCellForms: LiveData<List<CellForm>> = cellFormRepository.allCellForms
    val lastCellForm: LiveData<CellForm> = cellFormRepository.lastCellForm
    val allSelectOptionData: LiveData<List<SelectOptionData>> = selectOptionDataRepository.allSelectOptionData

    fun getCellFormsWithReportId(reportId: Long): LiveData<List<CellForm>> {
        return cellFormRepository.getCellFormsWithReportId(reportId)
    }

    fun getSelectOptionDataWithCellFormId(cellFormId: Long): LiveData<List<SelectOptionData>> {
        return selectOptionDataRepository.getSelectOptionDataWithCellFormId(cellFormId)
    }

    fun getCellFormWithId(cellFormId: Long): LiveData<CellForm> {
        return cellFormRepository.getCellFormWithId(cellFormId)
    }

    fun insertCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.insert(cellForm)
    }

    fun insertSelectOptionData(selectOptionData: SelectOptionData) = viewModelScope.launch(Dispatchers.IO) {
        selectOptionDataRepository.insert(selectOptionData)
    }

    fun deleteCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.delete(cellForm)
    }

    fun deleteSelectOptionData(selectOptionData: SelectOptionData) = viewModelScope.launch(Dispatchers.IO) {
        selectOptionDataRepository.delete(selectOptionData)
    }

    // TODO 나중에 DAO 단에서 Transaction 으로 구현하기
    fun updateTransaction(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        deleteCellForm(cellForm).join()
        insertCellForm(cellForm).join()
        for (sod in selectOptionDataCacheList) {
            insertSelectOptionData(sod)
        }
    }
}