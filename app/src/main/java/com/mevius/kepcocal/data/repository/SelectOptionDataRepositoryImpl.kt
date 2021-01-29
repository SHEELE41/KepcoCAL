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

    override fun getSelectOptionDataWithReportId(reportId: Long): LiveData<List<SelectOptionData>> {
        return selectOptionDataDao.getSelectOptionDataWithReportId(reportId)
    }

    override fun getSelectOptionDataWithCellFormId(cellFormId: Long): LiveData<List<SelectOptionData>> {
        return selectOptionDataDao.getSelectOptionDataWithCellFormId(cellFormId)
    }

    override fun getSelectOptionDataWithCellFormIdAndAutoFlag(
        cellFormId: Long,
        isAuto: Boolean
    ): LiveData<List<SelectOptionData>> {
        return selectOptionDataDao.getSelectOptionDataWithCellFormIdAndAutoFlag(cellFormId, isAuto)
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