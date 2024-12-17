package me.snowcube.tapped.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.snowcube.tapped.data.TasksRepository
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.ui.components.TaskDetailRoute
import javax.inject.Inject

/**
 * UiState for the Details screen.
 */
data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val isTaskDeleted: Boolean = false
)

/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route: TaskDetailRoute = savedStateHandle.toRoute()
    private val taskId: Long = route.taskId

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _userMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)
    private val _taskAsync = tasksRepository.getTaskStream(taskId)
        .map { handleTask(it) }
        .catch {
            emit(TaskState.Error(it))
        }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        _userMessage, _isLoading, _isTaskDeleted, _taskAsync
    ) { userMessage, isLoading, isTaskDeleted, taskAsync ->
        when (taskAsync) {
            TaskState.Loading -> {
                TaskDetailUiState(isLoading = true)
            }

            is TaskState.Error -> {
                TaskDetailUiState(
                    userMessage = taskAsync.exception.message,
                    isTaskDeleted = isTaskDeleted
                )
            }

            is TaskState.Success -> {
                TaskDetailUiState(
                    task = taskAsync.task,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    isTaskDeleted = isTaskDeleted
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = TaskDetailUiState(isLoading = true)
    )

    private fun handleTask(task: Task?): TaskState {
        if (task == null) {
            return TaskState.Error(Exception("Load task returned null"))
        }
        return TaskState.Success(task)
    }
}

// 数据状态的封装
sealed class TaskState {
    object Loading : TaskState()
    data class Success(val task: Task) : TaskState()
    data class Error(val exception: Throwable) : TaskState()
}
