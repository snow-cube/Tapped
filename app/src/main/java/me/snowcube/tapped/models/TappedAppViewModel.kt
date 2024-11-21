package me.snowcube.tapped.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.snowcube.tapped.data.TasksRepository
import me.snowcube.tapped.data.source.local.Task
import javax.inject.Inject

enum class NfcWritingState {
    Closed, Writing, Succeeded, Failed
}

data class TappedUiState(
    // TODO: 对 UI 状态分类嵌套
    val hasTaskProcess: Boolean = false,
    val runningTaskUiState: RunningTaskUiState = RunningTaskUiState(),

    val nfcWritingState: NfcWritingState = NfcWritingState.Closed,
)

data class RunningTaskUiState(
    val taskId: Int = 0,
    val isRunning: Boolean = false,
    val currentTaskTime: Int = 0,

    val taskCnt: Int = 0,
    val accumulatedTime: Int = 0,
)

fun Task.toRunningTaskUiState(): RunningTaskUiState {
    return RunningTaskUiState(
        taskId = this.id,
//        taskCnt =
//        accumulatedTime =
    )
}

// TODO: 应能够详细记录进程的暂停时间等信息
// 任务时长通过开始结束暂停信息计算
data class TaskProcessRecord(
    val startTime: Long, // 暂时为 Long，后续应该会替代为库的时间段数据结构
    val endTime: Long,
) {
    val runningTime: Long
        get() = endTime - startTime
}

@HiltViewModel
class TappedAppViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(TappedUiState())
    val uiState: StateFlow<TappedUiState> = _uiState.asStateFlow()

    // Handle business logic
    fun updateTaskProcessState(
        hasTaskProcess: Boolean,
        isRunning: Boolean,
        currentTaskTime: Int,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                hasTaskProcess = hasTaskProcess,
                runningTaskUiState = currentState.runningTaskUiState.copy(
                    isRunning = isRunning,
                    currentTaskTime = currentTaskTime,
                ),
            )
        }
    }

    // TODO: Temp logistic
    fun updateTaskRecord(
        runningTime: Int
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                runningTaskUiState = currentState.runningTaskUiState.copy(
                    taskCnt = currentState.runningTaskUiState.taskCnt + 1,
                    accumulatedTime = currentState.runningTaskUiState.accumulatedTime + runningTime
                ),
            )
        }
    }

    fun setWritingState(state: NfcWritingState) {
        _uiState.update { currentState ->
            currentState.copy(
                nfcWritingState = state
            )
        }
    }

    suspend fun getTask(taskId: Int): Task? =
        tasksRepository.getTaskStream(taskId).filterNotNull().firstOrNull()


    fun updateRunningTaskInfo(taskId: Int) {
        viewModelScope.launch {
            getTask(taskId)?.let {
                _uiState.update { currentState ->
                    currentState.copy(
                        runningTaskUiState = currentState.runningTaskUiState.copy(
                            taskId = it.id
                        )
                    )
                }
            }
        }
    }

    // TODO: Temporary logic 应在数据层实现任务记录，更新任务记录并在更新记录方法中检查完成状态 *所有记录更新操作中都应包含对完成状态的检查而非由调用者检查*
    fun performTaskOnce(taskId: Int, taskProcessRecord: TaskProcessRecord? = null) {
        viewModelScope.launch {
            getTask(taskId)?.let { task ->
                if (!task.isRepetitive) {
                    completeTask(taskId)
                }
            }
        }
    }

    fun completeTask(taskId: Int) {
        viewModelScope.launch {
            tasksRepository.completeTask(taskId)
        }
    }
}