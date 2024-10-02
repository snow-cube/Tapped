package com.example.nfc_task.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nfc_task.models.TaskUiState

enum class TaskAppScreen {
    Home,
    TaskDetail,
    Login
}

@Composable
fun TaskApp(
    onStartNewTask: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onWriteClick: () -> Unit,
    onSaveNewTaskClick: (String, String) -> Unit,
    uiState: TaskUiState,
    onCloseWritingClick: () -> Unit,
) {
    val taskAppNavController = rememberNavController()
    NavHost(
        navController = taskAppNavController,
        startDestination = TaskAppScreen.Home.name
    ) {
        composable(route = TaskAppScreen.Home.name) {
            TaskAppHome(
                onTaskItemClick = {
                    taskAppNavController.navigate(TaskAppScreen.TaskDetail.name)
                },
                onWriteClick = onWriteClick,
                onCloseWritingClick = onCloseWritingClick,
                uiState = uiState,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onBottomTaskControllerClick = {
                    taskAppNavController.navigate(TaskAppScreen.TaskDetail.name)
                },
                onSaveNewTaskClick = onSaveNewTaskClick
            )
        }
        composable(route = TaskAppScreen.TaskDetail.name) {
            TaskDetail(
                uiState = uiState,
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


