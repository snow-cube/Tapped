package me.snowcube.tapped.ui.components.task_list

import androidx.compose.runtime.Composable
import me.snowcube.tapped.data.source.local.Task

@Composable
fun PersonalTaskList(
    taskList: List<Task>, onTaskItemClick: (taskId: Int) -> Unit
) {
    // TODO: 直接封装到一个 folder 中
    TaskListBody(
        taskList = listOf(taskList, taskList, taskList, taskList),
        onTaskItemClick = onTaskItemClick,
    )
}