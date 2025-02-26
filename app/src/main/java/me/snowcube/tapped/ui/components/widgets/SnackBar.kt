package me.snowcube.tapped.ui.components.widgets

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


// Hassle-free snackbar in Jetpack Compose from https://www.kborowy.com/blog/easy-compose-snackbar/
// The part that displays a Snackbar from outside the Compose hasn't been imported yet.

//data class SnackbarAction(val title: String, val onActionPress: () -> Unit)


@Immutable
class SnackbarController(
    private val host: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    companion object {
        val current
            @Composable
            @ReadOnlyComposable
            get() = LocalSnackbarController.current
    }

//    fun showMessage(
//        message: String,
//        action: SnackbarAction? = null,
//        duration: SnackbarDuration = SnackbarDuration.Short,
//    ) {
//        scope.launch {
//            /**
//             * note: uncomment this line if you want snackbar to be displayed immediately,
//             * rather than being enqueued and waiting [duration] * current_queue_size
//             */
//            // host.currentSnackbarData?.dismiss()
//            val result =
//                host.showSnackbar(
//                    message = message,
//                    actionLabel = action?.title,
//                    duration = duration
//                )
//
//            if (result == SnackbarResult.ActionPerformed) {
//                action?.onActionPress?.invoke()
//            }
//        }
//    }

    fun showMessage(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        onActionPerformed: () -> Unit = {},
    ) {
        scope.launch {
            val result = host.showSnackbar(
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

val LocalSnackbarController = staticCompositionLocalOf {
    SnackbarController(
        host = SnackbarHostState(),
        scope = CoroutineScope(EmptyCoroutineContext)
    )
}

@Composable
fun SnackbarControllerProvider(content: @Composable (snackbarHost: SnackbarHostState) -> Unit) {
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackController = remember(scope) {
        SnackbarController(snackHostState, scope)
    }

    CompositionLocalProvider(LocalSnackbarController provides snackController) {
        content(
            snackHostState
        )
    }
}