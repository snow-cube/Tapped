package com.example.nfc_task.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class TaskAppScreen {
    Home,
    TaskDetail,
    Login
}

@Composable
fun TaskApp(
    hasTaskProcess: Boolean,
    isRunning: Boolean,
    currentTaskTime: Int,
    onStartNewTask: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
) {
    val taskAppNavController = rememberNavController()
    NavHost(
        navController = taskAppNavController,
        startDestination = TaskAppScreen.Home.name
    ) {
        composable(route = TaskAppScreen.Home.name) {
            TaskAppHome(onTaskItemClick = {
                taskAppNavController.navigate(TaskAppScreen.TaskDetail.name)
            })
        }
        composable(route = TaskAppScreen.TaskDetail.name) {
            TaskDetail(
                hasTaskProcess = hasTaskProcess,
                isRunning = isRunning,
                currentTaskTime = currentTaskTime,
                onStartNewTask = onStartNewTask,
                onContinueTask = onContinueTask,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask
            )
        }
        composable(route = TaskAppScreen.Login.name) {
            LoginPage()
        }
    }

}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}


