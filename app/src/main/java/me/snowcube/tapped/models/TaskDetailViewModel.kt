package me.snowcube.tapped.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.snowcube.tapped.data.TasksRepository
import me.snowcube.tapped.data.source.local.Task
import javax.inject.Inject

/**
 * UiState for the Details screen.
 */
data class TaskDetailUiState(
    val task: Task? = null, val isLoading: Boolean = false, val isTaskDeleted: Boolean = false
)

/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val tasksRepository: TasksRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    val taskId: Int = savedStateHandle.toRoute()

    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)
    private val _task = tasksRepository.getTaskStream(taskId)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        _isLoading, _isTaskDeleted, _task
    ) { isLoading, isTaskDeleted, task ->
        if (task != null) TaskDetailUiState(
            task = task, isLoading = isLoading, isTaskDeleted = isTaskDeleted
        )
        else TaskDetailUiState(isLoading = true)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TaskDetailViewModel.TIMEOUT_MILLIS),
        initialValue = TaskDetailUiState(isLoading = true)
    )

//    fun deleteTask() = viewModelScope.launch {
//        tasksRepository.deleteTask(taskId)
//        _isTaskDeleted.value = true
//    }

//    fun setCompleted(completed: Boolean) = viewModelScope.launch {
//        val task = uiState.value.task ?: return@launch
//        if (completed) {
//            tasksRepository.completeTask(task.id)
//            showSnackbarMessage(R.string.task_marked_complete)
//        } else {
//            tasksRepository.activateTask(task.id)
//            showSnackbarMessage(R.string.task_marked_active)
//        }
//    }

//    fun refresh() {
//        _isLoading.value = true
//        viewModelScope.launch {
//            tasksRepository.refreshTask(taskId)
//            _isLoading.value = false
//        }
//    }
}