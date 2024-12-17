package me.snowcube.tapped.ui.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskProcessRecord

@Serializable
object HomeRoute

@Serializable
data class TaskDetailRoute(val taskId: Long)

@Serializable
object LoginRoute

/**
 * The utility class is used to pass between composable functions to provide the functionality
 * of displaying a snackbar, as a replacement for passing lambda parameters in order to
 * solve the issue that lambda parameters do not support optional arguments.
 */
class SnackbarLauncher(
    val snackbarHostState: SnackbarHostState,
    private val snackbarScope: CoroutineScope,
) {
    fun launch(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        onActionPerformed: () -> Unit = {},
    ) {
        snackbarScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onActionPerformed()

                SnackbarResult.Dismissed -> {/* Handle snackbar dismissed */
                }
            }
        }
    }
}

@Composable
fun rememberSnackbarLauncher(
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }, snackbarScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, snackbarScope) {
    SnackbarLauncher(
        snackbarHostState = snackbarHostState, snackbarScope = snackbarScope
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TappedApp(
    onStartNewTask: (task: Task) -> Unit,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (
        taskId: Long, taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    writeTaskToNfc: (taskId: Long) -> Boolean,
    tappedUiState: TappedUiState,
    onCloseWritingClick: () -> Unit,
) {
    val taskAppNavController = rememberNavController()

    val snackbarLauncher: SnackbarLauncher = rememberSnackbarLauncher()

    Box() {
        NavHost(
            navController = taskAppNavController, startDestination = HomeRoute
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
                    snackbarLauncher = snackbarLauncher,
                )
            }
            composable<TaskDetailRoute> { backStackEntry ->
                TaskDetail(
                    navigateBack = { taskAppNavController.popBackStack() },
                    tappedUiState = tappedUiState,
                    onStartNewTask = onStartNewTask,
                    onContinueTask = onContinueTask,
                    finishTaskProcess = finishTaskProcess,
                    performTaskOnce = performTaskOnce,
                    onPauseTask = onPauseTask,
                    snackbarLauncher = snackbarLauncher,
                )
            }
            composable<LoginRoute> {
                LoginPage()
            }
        }

        SnackbarHost(
            hostState = snackbarLauncher.snackbarHostState, Modifier.align(Alignment.BottomCenter)
//                .safeDrawingPadding()
                .padding(bottom = 84.dp)
        ) { data ->
            // Fixing the problem that snackbar can't be on top of bottom sheet with Popup wrapping Snackbar
            Popup(
                alignment = Alignment.BottomCenter,
//                offset = IntOffset(0, -240)
            ) {
                // Custom snackbar with the custom action button color and border
                Snackbar(shape = MaterialTheme.shapes.extraLarge, action = {
                    TextButton(
                        onClick = { data.performAction() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(data.visuals.actionLabel ?: "")
                    }
                }, dismissAction = {
                    if (data.visuals.withDismissAction) {
                        IconButton(onClick = { data.dismiss() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss snackbar",
                            )
                        }
                    }
                }, modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    // The Material spec recommends a maximum of 2 lines of text.
                    Text(
                        data.visuals.message,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}


