package me.snowcube.tapped.data

import kotlinx.coroutines.flow.Flow
import me.snowcube.tapped.data.source.local.Task

/**
 * Repository that provides insert, update, delete, and retrieve of [Task] from a given data source.
 */
interface TasksRepository {
    /**
     * Retrieve all the tasks from the the given data source.
     */
    fun getAllTasksStream(): Flow<List<Task>>

    /**
     * Retrieve an task from the given data source that matches with the [id].
     */
    fun getTaskStream(id: Long): Flow<Task?>

    /**
     * Insert task in the data source
     */
    suspend fun insertTask(task: Task): Long

    /**
     * Delete task from the data source
     */
    suspend fun deleteTask(task: Task)

    /**
     * Update task in the data source
     */
    suspend fun updateTask(task: Task)

    suspend fun completeTask(taskId: Long)

//    suspend fun appendTaskRecord(taskId: Int, taskRecord)
}