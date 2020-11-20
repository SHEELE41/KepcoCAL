package com.mevius.kepcocal.di

import android.content.Context
import androidx.room.Room
import com.mevius.kepcocal.data.db.AppDatabase
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
}