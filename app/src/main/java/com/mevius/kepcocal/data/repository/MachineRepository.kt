package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.MachineDao
import com.mevius.kepcocal.data.db.entity.Machine

class MachineRepository constructor(
    private val localDataSource: MachineDao
){
    val allMachines: LiveData<List<Machine>> = localDataSource.getAll()

    suspend fun insert(machine: Machine){
        localDataSource.insert(machine)
    }

    suspend fun delete(machine: Machine){
        localDataSource.delete(machine)
    }
}