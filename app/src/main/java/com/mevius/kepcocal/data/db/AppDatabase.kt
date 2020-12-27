package com.mevius.kepcocal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mevius.kepcocal.data.db.dao.*
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project

@Database(entities = [Project::class, Machine::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun projectDao(): ProjectDao
    abstract fun machineDao(): MachineDao
    abstract fun reportDao(): ReportDao
    abstract fun cellFormDao(): CellFormDao
    abstract fun cellDataDao(): CellDataDao
}