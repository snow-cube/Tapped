package me.snowcube.tapped.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.snowcube.tapped.models.TappedUiState

enum class TappedAppScreen {
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
    uiState: TappedUiState,
    onCloseWritingClick: () -> Unit,
) {
    val taskAppNavController = rememberNavController()
    NavHost(
        navController = taskAppNavController,
        startDestination = TappedAppScreen.Home.name
    ) {
        composable(route = TappedAppScreen.Home.name) {
            TaskAppHome(
                onTaskItemClick = {
                    taskAppNavController.navigate(TappedAppScreen.TaskDetail.name)
                },
                onWriteClick = onWriteClick,
                onCloseWritingClick = onCloseWritingClick,
                uiState = uiState,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onBottomTaskControllerClick = {
                    taskAppNavController.navigate(TappedAppScreen.TaskDetail.name)
                },
                onSaveNewTaskClick = onSaveNewTaskClick
            )
        }
        composable(route = TappedAppScreen.TaskDetail.name) {
            TaskDetail(
                uiState = uiState,
                onStartNewTask = onStartNewTask,
                onContinueTask = onContinueTask,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask
            )
        }
        composable(route = TappedAppScreen.Login.name) {
            LoginPage()
        }
    }

}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}


