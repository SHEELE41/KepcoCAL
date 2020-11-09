package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.MachineDao
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Machine

class MachineRepository constructor(
    private val localDataSource: MachineDao
){
    // Singleton Pattern
    companion object {
        @Volatile
        private var instance: MachineRepository? = null

        fun getInstance(machineDao: MachineDao): MachineRepository {
            return instance ?: synchronized(this) {
                instance ?: MachineRepository(machineDao).also { instance = it }
            }
        }
    }
    val allMachines: LiveData<List<Machine>> = localDataSource.getAll()

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>>{
        return localDataSource.getMachinesWithProjectId(projectId)
    }

    suspend fun insert(machine: Machine){
        localDataSource.insert(machine)
    }

    suspend fun delete(machine: Machine){
        localDataSource.delete(machine)
    }
}