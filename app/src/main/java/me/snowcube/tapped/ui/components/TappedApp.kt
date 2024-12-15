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
import me.snowcube.tapped.models.TaskProcessRecord

//enum class TappedAppScreen {
//    Home,
//    TaskDetail,
//    Login
//}

@Serializable
object HomeRoute

@Serializable
data class TaskDetailRoute(val taskId: Long)

@Serializable
object LoginRoute


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TappedApp(
    onStartNewTask: (task: Task) -> Unit,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (
        taskId: Long,
        taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    writeTaskToNfc: (taskId: Long) -> Boolean,
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
                writeTaskToNfc = writeTaskToNfc,
                finishTaskProcess = finishTaskProcess,
                performTaskOnce = performTaskOnce,
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
                navigateBack = { taskAppNavController.popBackStack() },
                tappedUiState = tappedUiState,
                onStartNewTask = onStartNewTask,
                onContinueTask = onContinueTask,
                finishTaskProcess = finishTaskProcess,
                performTaskOnce = performTaskOnce,
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


