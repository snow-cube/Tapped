package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TaskDetail(
    hasTaskProcess: Boolean,
    isRunning: Boolean,
    currentTaskTime: Int,
    onStartNewTask: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit
) {
    // 须通过 Service 返回的状态控制按钮的 enable 状态
    // 若按钮 enabled 点击后会尝试调用服务接口
    // 开始新任务 按钮在未找到服务时会尝试创建服务并绑定
    // 其他按钮则会发出找不到服务的错误提示
    // TODO: 当前存在进行中任务同时开启新任务则警告是否终止前一任务
    // TODO: 终止 / 完成 同时只显示一个，终止按钮发出警告
    // TODO: 开始新任务 / 暂停 / 继续 同时只显示一个
    Column {
        Text("time: $hasTaskProcess")
        Button(
            enabled = !hasTaskProcess,
            onClick = onStartNewTask,
        ) {
            Text("start")
        }
        Button(
            enabled = hasTaskProcess && isRunning,
            onClick = onPauseTask,
        ) {
            Text("pause")
        }
        Button(
            enabled = hasTaskProcess && !isRunning,
            onClick = onContinueTask,
        ) {
            Text("continue")
        }
        Button(
            enabled = hasTaskProcess,
            onClick = onTerminateTask,
        ) {
            Text("stop")
        }
        Button(
            enabled = hasTaskProcess,
            onClick = onFinishTask,
        ) {
            Text("finish")
        }
    }
}