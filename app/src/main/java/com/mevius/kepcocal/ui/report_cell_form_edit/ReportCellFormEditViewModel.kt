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
    // Activity - Fragment 데이터 공유를 위한 공용 변수 선언
    var reportId = 0L
    var cellFormId = 0L
    var typeTwoSelectOptionDataCacheList = ArrayList<SelectOptionData>()
    var typeThreeSelectOptionDataPosition = 1

    // TODO 안쓰는 것 정리하기
    val lastCellForm: LiveData<CellForm> = cellFormRepository.lastCellForm

    fun getSelectOptionDataByCellFormIdAndAutoFlag(cellFormId: Long, isAuto: Boolean): LiveData<List<SelectOptionData>> {
        return selectOptionDataRepository.getSelectOptionDataByCellFormIdAndAutoFlag(cellFormId, isAuto)
    }

    fun getCellFormById(cellFormId: Long): LiveData<CellForm> {
        return cellFormRepository.getCellFormById(cellFormId)
    }

    private fun insertCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.insert(cellForm)
    }

    private fun insertSelectOptionData(selectOptionData: SelectOptionData) = viewModelScope.launch(Dispatchers.IO) {
        selectOptionDataRepository.insert(selectOptionData)
    }

    private fun deleteCellForm(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        cellFormRepository.delete(cellForm)
    }

    // TODO 나중에 DAO 단에서 Transaction 으로 구현하기
    fun updateTransaction(cellForm: CellForm) = viewModelScope.launch(Dispatchers.IO) {
        deleteCellForm(cellForm).join()     // SelectOptionData CASCADE
        insertCellForm(cellForm).join()
        // Type2 작업
        for (sod in typeTwoSelectOptionDataCacheList) {
            insertSelectOptionData(sod)
        }
        // Type3 작업
        insertSelectOptionData(
            SelectOptionData(
                null,
                cellFormId,
                reportId,
                true,
                typeThreeSelectOptionDataPosition.toString()
            )
        )
    }
}