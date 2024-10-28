package me.snowcube.tapped.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TappedApp(
    onStartNewTask: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onWriteClick: () -> Unit,
    tappedUiState: TappedUiState,
    onCloseWritingClick: () -> Unit,
) {
    val taskAppNavController = rememberNavController()
    NavHost(
        navController = taskAppNavController,
        startDestination = TappedAppScreen.Home.name
    ) {
        composable(route = TappedAppScreen.Home.name) {
            TappedAppHome(
                onTaskItemClick = {
                    taskAppNavController.navigate(TappedAppScreen.TaskDetail.name)
                },
                onWriteClick = onWriteClick,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onBottomTaskControllerClick = {
                    taskAppNavController.navigate(TappedAppScreen.TaskDetail.name)
                },
                tappedUiState = tappedUiState,
                onCloseWritingClick = onCloseWritingClick,
            )
        }
        composable(route = TappedAppScreen.TaskDetail.name) {
            TaskDetail(
                uiState = tappedUiState,
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


