package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.SelectOptionData

interface SelectOptionDataRepository {
    val allSelectOptionData: LiveData<List<SelectOptionData>>

    fun getSelectOptionDataByReportId(reportId: Long): LiveData<List<SelectOptionData>>

    fun getSelectOptionDataByCellFormIdAndAutoFlag(cellFormId: Long, isAuto: Boolean): LiveData<List<SelectOptionData>>

    suspend fun insert(selectOptionData: SelectOptionData)

    suspend fun update(selectOptionData: SelectOptionData)

    suspend fun delete(selectOptionData: SelectOptionData)
}