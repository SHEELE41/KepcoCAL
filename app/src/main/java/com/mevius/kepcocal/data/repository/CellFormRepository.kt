package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.CellForm

interface CellFormRepository {
    val allCellForm: LiveData<List<CellForm>>

    suspend fun insert(cellForm: CellForm)

    suspend fun delete(cellForm: CellForm)
}