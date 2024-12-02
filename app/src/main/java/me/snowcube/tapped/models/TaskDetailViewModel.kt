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

///**
// * UiState for the Details screen.
// */
//data class TaskDetailUiState(
//    val taskState: TaskState = TaskState.Loading,
//    val isLoading: Boolean = true,
//    val errorMessage: String? = null
//)
//
///**
// * ViewModel for the Details screen.
// */
//@HiltViewModel
//class TaskDetailViewModel @Inject constructor(
//    private val tasksRepository: TasksRepository,
//    private val savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val route : TaskDetailRoute = savedStateHandle.toRoute()
//
//    // Expose screen UI state
//    private val _uiState = MutableStateFlow(TaskDetailUiState())
//    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()
//
//    // 定义一个 StateFlow 来存储 Task UI 状态
//    private val _taskUiState = MutableStateFlow<TaskState>(TaskState.Loading)
//    val taskUiState: StateFlow<TaskState> = _taskUiState
//
//    // 初始化函数，传入 id 从数据库获取数据并监听更新
//    fun loadTask(taskId: Long) {
//        _uiState.value = _uiState.value.copy(isLoading = true) // 设置加载状态
//
//        viewModelScope.launch {
//            try {
//                tasksRepository.getTaskStream(taskId)  // 数据库查询返回 Flow
//                    .collect { data ->
//                        // 更新成功状态的 UI State
//                        _uiState.value = if (data != null) TaskDetailUiState(
//                            taskState = TaskState.Success(data),
//                            isLoading = false
//                        ) else TaskDetailUiState(
//                            taskState = TaskState.Error(Exception("Load task returned null")),
//                            isLoading = false,
//                            errorMessage = "Load task returned null"
//                        )
//                    }
//            } catch (e: Throwable) {
//                // 更新错误状态的 UI State
//                _uiState.value = TaskDetailUiState(
//                    taskState = TaskState.Error(e),
//                    isLoading = false,
//                    errorMessage = e.message
//                )
//            }
//        }
//    }
//}
//
//// 数据状态的封装
//sealed class TaskState {
//    object Loading : TaskState()
//    data class Success(val task: Task) : TaskState()
//    data class Error(val exception: Throwable) : TaskState()
//}


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
