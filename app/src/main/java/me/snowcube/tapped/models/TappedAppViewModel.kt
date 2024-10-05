package me.snowcube.tapped.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class WriteState {
    Closed,
    Writing,
    Succeeded,
    Failed
}

data class TappedUiState(
    // TODO: 对 UI 状态分类嵌套
    val hasTaskProcess: Boolean = false,
    val isRunning: Boolean = false,
    val currentTaskTime: Int = 0,

    val taskCnt: Int = 0,
    val accumulatedTime: Int = 0,

    val writeState: WriteState = WriteState.Closed
)

class TappedAppViewModel : ViewModel() {

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
                isRunning = isRunning,
                currentTaskTime = currentTaskTime,
            )
        }
    }

    // TODO: Temp logistic
    fun updateTaskRecord(
        runningTime: Int
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                taskCnt = currentState.taskCnt + 1,
                accumulatedTime = currentState.accumulatedTime + runningTime
            )
        }
    }

    fun saveTask() {
        // TODO: Save task
    }

    fun setWritingState(state: WriteState) {
        _uiState.update { currentState ->
            currentState.copy(
                writeState = state
            )
        }
    }
}