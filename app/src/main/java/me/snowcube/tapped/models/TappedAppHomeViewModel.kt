package me.snowcube.tapped.models

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    val testState: Int = 0,
//    val addTaskUiState: AddTaskUiState = AddTaskUiState(),
)

data class EditTaskUiState(
    val taskDetails: TaskDetailsUiState = TaskDetailsUiState(),
    // 其他 UI 状态，例如是否可编辑
)

data class TaskDetailsUiState(
    val selectedStartTime: Pair<Int, Int>? = null,
    val selectedEndTime: Pair<Int, Int>? = null,
    val beginDateSelected: Long? = null,
    val endDateSelected: Long? = null,
    val taskTitle: String = "",
    val taskDescription: String = "",
    val switchSelected: String = "NFC",
    val isContinuous: Boolean = false,
    val isRepetitive: Boolean = false,
)

fun TaskDetailsUiState.inNfcManner(): Boolean = switchSelected == "NFC"

/**
 * Extension function to convert [TaskDetailsUiState] to [Task].
 */
fun TaskDetailsUiState.toTask(): Task = Task(
    taskTitle = taskTitle,
    taskDescription = taskDescription,
    taskTime = "",
    inNfcManner = inNfcManner(),
    isRepetitive = isRepetitive,
    isContinuous = isContinuous, // TODO: 暂时将 NFC 任务设为持续，否则非持续
    isCompleted = false
)

@HiltViewModel
open class TappedAppHomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Expose screen UI state
    private val _uiState = MutableStateFlow(TappedAppHomeUiState())
    val uiState: StateFlow<TappedAppHomeUiState> = _uiState.asStateFlow()

    /**
     * Holds current task editing UI state
     */
    var editTaskUiState by mutableStateOf(EditTaskUiState())
        private set

    /**
     * Updates the [editTaskUiState] with the value provided in the argument. This method also triggers
     * a validation for input values. // TODO: 输入验证
     */
    fun updateEditTaskUiState(taskDetails: TaskDetailsUiState) {
        editTaskUiState =
            EditTaskUiState(
                taskDetails = taskDetails,
//                isEntryValid = validateInput(taskDetails)
            )
    }

    val taskListUiState: StateFlow<TaskListUiState> =
        tasksRepository.getAllTasksStream().map { TaskListUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TaskListUiState()
        )

    suspend fun saveTask(): Long {
        // TODO: 验证输入
        return tasksRepository.insertTask(editTaskUiState.taskDetails.toTask())
    }

//    fun updateAddTaskUiState(editTaskUiState: EditTaskUiState) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                addTaskUiState = editTaskUiState
//            )
//        }
//    }
}