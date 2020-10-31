package com.mevius.kepcocal.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mevius.kepcocal.data.db.entity.Machine

@Dao
interface MachineDao {
    @Query("SELECT * FROM Machine")
    fun getAll(): List<Machine>

    @Insert
    fun insertAll(vararg machines: Machine)

    @Delete
    fun delete(machine: Machine)
}