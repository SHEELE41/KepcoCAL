package com.mevius.kepcocal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mevius.kepcocal.data.db.dao.*
import com.mevius.kepcocal.data.db.entity.*

@Database(entities = [Project::class, Machine::class, Report::class, CellData::class, CellForm::class, SelectOptionData::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){
    abstract fun projectDao(): ProjectDao
    abstract fun machineDao(): MachineDao
    abstract fun reportDao(): ReportDao
    abstract fun cellFormDao(): CellFormDao
    abstract fun cellDataDao(): CellDataDao
    abstract fun selectOptionDataDao(): SelectOptionDataDao
}