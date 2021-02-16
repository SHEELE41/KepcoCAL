package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.SelectOptionDataDao
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import javax.inject.Inject

class SelectOptionDataRepositoryImpl @Inject constructor(
    private val selectOptionDataDao: SelectOptionDataDao
) : SelectOptionDataRepository {
    override val allSelectOptionData: LiveData<List<SelectOptionData>> =
        selectOptionDataDao.getAll()

    override fun getSelectOptionDataByReportId(reportId: Long): LiveData<List<SelectOptionData>> {
        return selectOptionDataDao.getSelectOptionDataByReportId(reportId)
    }

    override fun getSelectOptionDataByCellFormIdAndAutoFlag(
        cellFormId: Long,
        isAuto: Boolean
    ): LiveData<List<SelectOptionData>> {
        return selectOptionDataDao.getSelectOptionDataByCellFormIdAndAutoFlag(cellFormId, isAuto)
    }

    override suspend fun insert(selectOptionData: SelectOptionData) {
        selectOptionDataDao.insert(selectOptionData)
    }

    override suspend fun update(selectOptionData: SelectOptionData) {
        selectOptionDataDao.update(selectOptionData)
    }

    override suspend fun delete(selectOptionData: SelectOptionData) {
        selectOptionDataDao.delete(selectOptionData)
    }
}