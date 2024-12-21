package me.snowcube.tapped.ui.components.task_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.snowcube.tapped.data.source.local.Task

@Composable
fun PersonalTaskList(
    taskList: List<Task>, onTaskItemClick: (taskId: Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TaskListBody(
            taskList = listOf(taskList, taskList, taskList, taskList),
            onTaskItemClick = onTaskItemClick,
        )
    }
}