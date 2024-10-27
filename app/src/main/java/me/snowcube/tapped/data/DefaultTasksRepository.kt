package me.snowcube.tapped.data

import kotlinx.coroutines.flow.Flow
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.data.source.local.TaskDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [TasksRepository]. Single entry point for managing tasks' data.
 *
 * @param localDataSource - The local data source
 */
@Singleton
class DefaultTasksRepository @Inject constructor(
    private val localDataSource: TaskDao,
) : TasksRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = localDataSource.getAllTasks()

    override fun getTaskStream(id: Int): Flow<Task?> = localDataSource.getTask(id)

    override suspend fun insertTask(task: Task) = localDataSource.insert(task)

    override suspend fun deleteTask(task: Task) = localDataSource.delete(task)

    override suspend fun updateTask(task: Task) = localDataSource.update(task)

}