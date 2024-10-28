package me.snowcube.tapped.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import me.snowcube.tapped.data.TasksRepository
import me.snowcube.tapped.data.source.local.Task
import javax.inject.Inject

data class TaskListUiState(val taskList: List<Task> = listOf())

data class TappedAppHomeUiState(
    // TODO: 对 UI 状态分类嵌套

    val addTaskUiState: AddTaskUiState = AddTaskUiState(),
)

data class AddTaskUiState(
    val selectedTime: Pair<Int, Int>? = null,
    val beginDateSelected: Long? = null,
    val endDateSelected: Long? = null,
    val taskTitle: String = "",
    val taskDescription: String = "",
    val switchSelected: String = "NFC"
)

fun AddTaskUiState.inNfcManner(): Boolean = switchSelected == "NFC"

/**
 * Extension function to convert [AddTaskUiState] to [Task].
 */
fun AddTaskUiState.toTask(): Task = Task(
    taskTitle = taskTitle, taskTime = "", inNfcManner = inNfcManner(), isPeriod = false
)

@HiltViewModel
class TappedAppHomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Expose screen UI state
    private val _uiState = MutableStateFlow(TappedAppHomeUiState())
    val uiState: StateFlow<TappedAppHomeUiState> = _uiState.asStateFlow()

    val taskListUiState: StateFlow<TaskListUiState> =
        tasksRepository.getAllTasksStream().map { TaskListUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TaskListUiState()
        )

    suspend fun saveTask() {
        tasksRepository.insertTask(uiState.value.addTaskUiState.toTask())
    }

    fun updateAddTaskUiState(addTaskUiState: AddTaskUiState) {
        _uiState.update { currentState ->
            currentState.copy(
                addTaskUiState = addTaskUiState
            )
        }
    }
}