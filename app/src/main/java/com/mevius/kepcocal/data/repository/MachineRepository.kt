package com.mevius.kepcocal.data.repository

import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import kotlinx.coroutines.CoroutineScope

interface MachineRepository {
    val allMachines: LiveData<List<Machine>>

    fun getMachinesWithProjectId(projectId: Long): LiveData<List<Machine>>

    suspend fun insert(machine: Machine)

    suspend fun update(machine: Machine)

    suspend fun delete(machine: Machine)

    suspend fun insertMachinesFromExcel(scope: CoroutineScope, project: Project)
}