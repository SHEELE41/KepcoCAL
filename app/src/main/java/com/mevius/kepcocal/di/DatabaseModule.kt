package com.mevius.kepcocal.di

import android.content.Context
import androidx.room.Room
import com.mevius.kepcocal.data.db.AppDatabase
import com.mevius.kepcocal.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "app_database"
    ).build()

    @Provides
    @Singleton
    fun provideMachineDao(db: AppDatabase) = db.machineDao()

    @Provides
    @Singleton
    fun provideProjectDao(db: AppDatabase) = db.projectDao()

    @Provides
    @Singleton
    fun provideReportDao(db: AppDatabase) = db.reportDao()

    @Provides
    @Singleton
    fun provideCellFormDao(db: AppDatabase) = db.cellFormDao()

    @Provides
    @Singleton
    fun provideCellDataDao(db: AppDatabase) = db.cellDataDao()

    @Provides
    @Singleton
    fun provideSelectOptionDataDao(db: AppDatabase) = db.selectOptionDataDao()

    @Provides
    @Singleton
    fun provideMachineRepository(machineRepository: MachineRepositoryImpl): MachineRepository =
        machineRepository

    @Provides
    @Singleton
    fun provideProjectRepository(projectRepository: ProjectRepositoryImpl): ProjectRepository =
        projectRepository

    @Provides
    @Singleton
    fun provideReportRepository(reportRepository: ReportRepositoryImpl): ReportRepository =
        reportRepository

    @Provides
    @Singleton
    fun provideCellFormRepository(cellFormRepository: CellFormRepositoryImpl): CellFormRepository =
        cellFormRepository

    @Provides
    @Singleton
    fun provideCellDataRepository(cellDataRepository: CellDataRepositoryImpl): CellDataRepository =
        cellDataRepository

    @Provides
    @Singleton
    fun provideSelectOptionDataRepository(selectOptionDataRepository: SelectOptionDataRepositoryImpl): SelectOptionDataRepository =
        selectOptionDataRepository
}