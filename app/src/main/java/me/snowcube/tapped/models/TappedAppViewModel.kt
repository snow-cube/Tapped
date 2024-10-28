package me.snowcube.tapped.models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.snowcube.tapped.data.TasksRepository
import javax.inject.Inject

enum class NfcWritingState {
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

    val nfcWritingState: NfcWritingState = NfcWritingState.Closed,
)

@HiltViewModel
class TappedAppViewModel @Inject constructor (
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

    fun setWritingState(state: NfcWritingState) {
        _uiState.update { currentState ->
            currentState.copy(
                nfcWritingState = state
            )
        }
    }
}