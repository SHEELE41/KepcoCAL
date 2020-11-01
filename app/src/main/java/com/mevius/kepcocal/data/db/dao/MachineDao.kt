package com.mevius.kepcocal.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mevius.kepcocal.data.db.entity.Machine

@Dao
interface MachineDao {
    @Query("SELECT * FROM Machine")
    fun getAll(): LiveData<List<Machine>>

    @Insert
    fun insert(machine: Machine)

    @Delete
    fun delete(machine: Machine)
}