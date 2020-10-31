package com.mevius.kepcocal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mevius.kepcocal.data.db.dao.MachineDao
import com.mevius.kepcocal.data.db.dao.ProjectDao
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Project::class, Machine::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun projectDao(): ProjectDao
    abstract fun machineDao(): MachineDao

    // 데이터베이스가 처음 Open 되었을 때 실행되는 콜백 함수.
    // 여기서 읽어오는 건가?
    // onCreate 초기화도 가능.
    /*
    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var projectDao = database.projectDao()
                }
            }
        }
    }
    */

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // .addCallback(WordDatabaseCallback(scope)) 콜백 붙이기 가능
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun destroyDatabase() {
            INSTANCE = null
        }
    }
}