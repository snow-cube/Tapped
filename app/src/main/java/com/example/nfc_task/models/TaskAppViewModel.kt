package com.example.nfc_task.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TaskUiState(
    // TODO: 对 UI 状态分类嵌套
    val hasTaskProcess: Boolean = false,
    val isRunning: Boolean = false,
    val currentTaskTime: Int = 0,
)

class TaskAppViewModel : ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

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
}