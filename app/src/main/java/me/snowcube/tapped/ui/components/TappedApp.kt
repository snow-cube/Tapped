package me.snowcube.tapped.ui.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import me.snowcube.tapped.data.source.local.Task
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
    onStartNewTask: (task: Task) -> Unit,
    finishTaskProcess: () -> Unit,
    completeTask: (taskId: Int) -> Unit,
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
                finishTaskProcess = finishTaskProcess,
                completeTask = completeTask,
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
            Log.d("TappedApp", "Before toRoute()")
            val route: TaskDetailRoute = backStackEntry.toRoute()
            Log.d("TappedApp", "After toRoute()")
            TaskDetail(
                tappedUiState = tappedUiState,
                onStartNewTask = onStartNewTask,
                onContinueTask = onContinueTask,
                finishTaskProcess = finishTaskProcess,
                completeTask = completeTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
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


