package me.snowcube.tapped.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.snowcube.tapped.R
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.models.RunningTaskUiState
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskDetailViewModel
import me.snowcube.tapped.models.TaskProcessRecord
import me.snowcube.tapped.models.TaskState
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor

@Composable
fun TaskDetail(
    onStartNewTask: (task: Task) -> Unit,
    finishTaskProcess: () -> TaskProcessRecord?, // 将持续任务的运行进程成功结束，返回运行记录
    performTaskOnce: (
        taskId: Long,
        taskProcessRecord: TaskProcessRecord?
    ) -> Unit, // 将任务打卡一次
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    tappedUiState: TappedUiState,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    // TaskDetail 的编辑任务 UI State 应和新建任务的分开，互不干扰

    // 须通过 Service 返回的状态控制按钮的 enable 状态
    // 若按钮 enabled 点击后会尝试调用服务接口
    // 开始新任务 按钮在未找到服务时会尝试创建服务并绑定
    // 其他按钮则会发出找不到服务的错误提示
    // TODO: 当前存在进行中任务同时开启新任务则警告是否终止前一任务
    // TODO: 终止 / 完成 同时只显示一个，终止按钮发出警告
    // TODO: 开始新任务 / 暂停 / 继续 同时只显示一个

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isRelatedTaskHasProcess =
        tappedUiState.hasTaskProcess && tappedUiState.runningTaskUiState.task?.id == uiState.task?.id

    val taskStateText =
        if (isRelatedTaskHasProcess) if (tappedUiState.runningTaskUiState.isRunning) "进行中" else "暂停中"
        else "未开始"
    val formattedTime =
        if (isRelatedTaskHasProcess) DateUtils.formatElapsedTime(tappedUiState.runningTaskUiState.currentTaskTime.toLong())
        else DateUtils.formatElapsedTime(0)

    val formattedAccumulatedTime = DateUtils.formatElapsedTime(0)

    val stateColor =
        if (isRelatedTaskHasProcess) if (tappedUiState.runningTaskUiState.isRunning) paletteColor.backgroundGreen else paletteColor.backgroundYellow
        else paletteColor.backgroundBlue

    Scaffold(
        containerColor = stateColor,
    ) { innerPadding ->
        Surface(
            color = stateColor, modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Surface(
                    color = Color(0x0C000000),
                    shape = RoundedCornerShape(500.dp),
                    modifier = Modifier.size(290.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            taskStateText,
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            formattedTime,
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "累计次数 ${uiState.task?.id}    |    累计时间 $formattedAccumulatedTime",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                    Text(
                        uiState.task?.taskTitle ?: "ERROR",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(50.dp)
                ) {
                    if (uiState.task?.isContinuous == true) {
                        IconButton(
                            enabled = true, // TODO: 非 NFC
                            onClick = {
                                if (!tappedUiState.hasTaskProcess) {
                                    onStartNewTask(uiState.task!!)
                                } else {
                                    if (isRelatedTaskHasProcess) { // 是本任务
                                        if (tappedUiState.runningTaskUiState.isRunning) {
                                            onPauseTask()
                                        } else {
                                            onContinueTask()
                                        }
                                    } else {
                                        // TODO: 警告是否终止 / 完成已存在任务并开启本任务
                                    }
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White,
                                containerColor = Color(0x0C000000),
                                disabledContentColor = Color(0x00000000),
                            ),
//                        modifier = Modifier
//                            .size(220.dp)
//                            .padding(50.dp)
                        ) {
                            Icon(
                                imageVector = if (isRelatedTaskHasProcess && tappedUiState.runningTaskUiState.isRunning) ImageVector.vectorResource(
                                    R.drawable.baseline_pause_24
                                )
                                else Icons.Default.PlayArrow,
                                contentDescription = "Start / Pause / Continue",
                                modifier = Modifier.fillMaxSize()
//                                .size(120.dp)
                            )
                        }
                        IconButton(
                            enabled = isRelatedTaskHasProcess,
                            onClick = {
                                if (true /* TODO: 非 NFC */) {
                                    uiState.task?.let { task ->
                                        val record = finishTaskProcess()
                                        performTaskOnce(task.id, record)
                                    }
                                } else {
                                    // TODO: 警告并确认是否终止进行中的持续任务进程
                                    finishTaskProcess()
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White,
                                containerColor = Color(0x0C000000),
                                disabledContentColor = Color(0x00000000),
                            ),
//                        modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = if (true /* TODO: 非 NFC */) Icons.Default.Done
                                else ImageVector.vectorResource(R.drawable.baseline_stop_24),
                                contentDescription = "Finish / Terminate",
                                modifier = Modifier.fillMaxSize()
//                                .size(70.dp)
                            )
                        }
                    } else {
                        IconButton(
                            enabled = true, // TODO: 任务非 NFC
                            onClick = {
                                uiState.task?.let { task -> performTaskOnce(task.id, null) }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White,
                                containerColor = Color(0x0C000000),
                                disabledContentColor = Color(0x00000000),
                            ),
//                        modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Complete the task",
                                modifier = Modifier.fillMaxSize()
//                                .size(70.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskDetailRunningPreview() {
    TappedTheme() {
        TaskDetail(
            onStartNewTask = {},
            finishTaskProcess = { null },
            onPauseTask = {},
            onContinueTask = {},
            tappedUiState = TappedUiState(
                hasTaskProcess = true,
                runningTaskUiState = RunningTaskUiState(
                    isRunning = true, taskCnt = 1, accumulatedTime = 110
                ),
            ),
            performTaskOnce = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun TaskDetailPausedPreview() {
    TappedTheme() {
        TaskDetail(
            onStartNewTask = {},
            finishTaskProcess = { null },
            onPauseTask = {},
            onContinueTask = {},
            tappedUiState = TappedUiState(
                hasTaskProcess = true,
                runningTaskUiState = RunningTaskUiState(
                    isRunning = false, taskCnt = 2, accumulatedTime = 130
                ),
            ),
            performTaskOnce = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun TaskDetailNotActivePreview() {
    TappedTheme() {
        TaskDetail(
            onStartNewTask = {},
            finishTaskProcess = { null },
            onPauseTask = {},
            onContinueTask = {},
            tappedUiState = TappedUiState(
                hasTaskProcess = false,
                runningTaskUiState = RunningTaskUiState(
                    isRunning = false, taskCnt = 2, accumulatedTime = 130
                ),
            ),
            performTaskOnce = { _, _ -> },
        )
    }
}