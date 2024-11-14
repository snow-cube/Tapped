package me.snowcube.tapped.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import me.snowcube.tapped.models.TappedUiState

//enum class TappedAppScreen {
//    Home,
//    TaskDetail,
//    Login
//}

@Serializable
object HomeRoute

@Serializable
data class TaskDetailRoute(val taskId: Int)

@Serializable
object LoginRoute


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TappedApp(
    onStartNewTask: (taskId: Int) -> Unit,
    finishTask: (taskId: Int, isContinuous: Boolean) -> Unit,
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
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            TappedAppHome(
                onTaskItemClick = { taskId ->
                    taskAppNavController.navigate(TaskDetailRoute(taskId))
                },
                onWriteClick = onWriteClick,
                finishTask = finishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onBottomTaskControllerClick = { taskId ->
                    taskAppNavController.navigate(TaskDetailRoute(taskId))
                },
                tappedUiState = tappedUiState,
                onCloseWritingClick = onCloseWritingClick,
            )
        }
        composable<TaskDetailRoute> { backStackEntry ->
            val taskId: Int = backStackEntry.toRoute()
            TaskDetail(
                taskId = taskId,
                tappedUiState = tappedUiState,
                onStartNewTask = onStartNewTask,
                onContinueTask = onContinueTask,
                finishTask = finishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask
            )
        }
        composable<LoginRoute> {
            LoginPage()
        }
    }

}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}


