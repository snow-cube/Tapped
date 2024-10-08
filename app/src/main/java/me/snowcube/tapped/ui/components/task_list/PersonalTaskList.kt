package me.snowcube.tapped.ui.components.task_list

import androidx.compose.runtime.Composable
import me.snowcube.tapped.data.Task


val folders = mutableListOf(
    mutableListOf(
        Task(
            inNfcManner = true,
            isPeriod = true,
            isRepeat = false,
            taskName = "测试任务1",
            taskTime = "9/17 19:00 - 19:20",
        ),
        Task(
            inNfcManner = true,
            isPeriod = false,
            isRepeat = false,
            taskName = "测试任务2一次性",
            taskTime = "9/18 18:30",
        ),
        Task(
            inNfcManner = true,
            isPeriod = true,
            isRepeat = true,
            taskName = "测试任务重复执行",
            taskTime = "9/17 21:00 - 21:20",
        ),
        Task(
            inNfcManner = false,
            isPeriod = false,
            isRepeat = false,
            taskName = "测试任务3非NFC",
            taskTime = "9/20 20:00",
        )
    ),
    mutableListOf(
        Task(
            inNfcManner = true,
            isPeriod = false,
            isRepeat = false,
            taskName = "Task name example",
            taskTime = "9/17 15:00",
        ),
    ),
    mutableListOf(
        Task(
            inNfcManner = false,
            isPeriod = false,
            isRepeat = false,
            taskName = "任务不重复一次性",
            taskTime = "9/17 18:20",
        ),
        Task(
            inNfcManner = false,
            isPeriod = false,
            isRepeat = false,
            taskName = "task name",
            taskTime = "9/17 18:30",
        ),
        Task(
            inNfcManner = false,
            isPeriod = false,
            isRepeat = false,
            taskName = "测试任务",
            taskTime = "9/20 20:00",
        )
    )
)

@Composable
fun PersonalTaskList(onTaskItemClick: () -> Unit) {
    TaskListBody(onTaskItemClick = onTaskItemClick, folders = folders)
}