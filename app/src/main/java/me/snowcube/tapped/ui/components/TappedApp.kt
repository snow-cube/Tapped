package me.snowcube.tapped.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskProcessRecord
import me.snowcube.tapped.ui.components.widgets.SnackbarControllerProvider

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
        taskId: Long, taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    writeTaskToNfc: (taskId: Long) -> Boolean,
    tappedUiState: TappedUiState,
    onCloseWritingClick: () -> Unit,
) {
    val taskAppNavController = rememberNavController()

//    val snackbarLauncher: SnackbarLauncher = rememberSnackbarLauncher()
    SnackbarControllerProvider { host ->
        Box() {
            NavHost(
                navController = taskAppNavController,
                startDestination = HomeRoute,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    fadeOut(
                        tween(500)
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        tween(500)
                    )
                },
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

            SnackbarHost(
                hostState = host, Modifier
                    .align(Alignment.BottomCenter)
//                .safeDrawingPadding()
                    .padding(bottom = 84.dp)
            ) { data ->
                // Fixing the problem that snackbar can't be on top of bottom sheet with Popup wrapping Snackbar
                Popup(
                    alignment = Alignment.BottomCenter,
//                offset = IntOffset(0, -240)
                ) {
                    ElevatedCard(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = Color.Red,
                            disabledContainerColor = Color.Red,
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(45.dp)
                                .padding(vertical = 3.dp)
                        ) {
                            // The Material spec recommends a maximum of 2 lines of text.
                            Text(
                                data.visuals.message,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 18.dp)
                            )
                            if (data.visuals.actionLabel != null) {
                                TextButton(
                                    onClick = { data.performAction() },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.tertiary
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text(
                                        data.visuals.actionLabel!!,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                            if (data.visuals.withDismissAction) {
                                IconButton(
                                    onClick = { data.dismiss() },
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss snackbar",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}


