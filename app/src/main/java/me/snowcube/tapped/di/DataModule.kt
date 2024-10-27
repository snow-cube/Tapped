package me.snowcube.tapped.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.snowcube.tapped.data.DefaultTasksRepository
import me.snowcube.tapped.data.source.local.TaskDao
import me.snowcube.tapped.data.source.local.AppDatabase
import me.snowcube.tapped.data.TasksRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTasksRepository(repository: DefaultTasksRepository) : TasksRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase) : TaskDao {
        return appDatabase.taskDao()
    }
}