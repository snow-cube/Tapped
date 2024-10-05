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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.snowcube.tapped.R
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor

@Composable
fun TaskDetail(
    onStartNewTask: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    uiState: TappedUiState
) {
    // 须通过 Service 返回的状态控制按钮的 enable 状态
    // 若按钮 enabled 点击后会尝试调用服务接口
    // 开始新任务 按钮在未找到服务时会尝试创建服务并绑定
    // 其他按钮则会发出找不到服务的错误提示
    // TODO: 当前存在进行中任务同时开启新任务则警告是否终止前一任务
    // TODO: 终止 / 完成 同时只显示一个，终止按钮发出警告
    // TODO: 开始新任务 / 暂停 / 继续 同时只显示一个

    val taskStateText = if (uiState.hasTaskProcess)
        if (uiState.isRunning) "进行中" else "暂停中"
    else "未开始"
    val formattedTime = DateUtils.formatElapsedTime(uiState.currentTaskTime.toLong())

    val formattedAccumulatedTime = DateUtils.formatElapsedTime(uiState.accumulatedTime.toLong())

    val stateColor = if (uiState.hasTaskProcess)
        if (uiState.isRunning) paletteColor.backgroundGreen else paletteColor.backgroundYellow
    else paletteColor.backgroundBlue

    Scaffold(
        containerColor = stateColor,
    ) { innerPadding ->
        Surface(
            color = stateColor,
            modifier = Modifier.padding(innerPadding)
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
                            style = MaterialTheme.typography.headlineMedium,
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
                        "累计次数 ${uiState.taskCnt}    |    累计时间 $formattedAccumulatedTime",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                    Text(
                        "测试任务 1",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(50.dp)
                ) {
                    IconButton(
                        enabled = true,
                        onClick = {
                            if (!uiState.hasTaskProcess) {
                                onStartNewTask()
                            } else {
                                if (true /* TODO: 是本任务 */) {
                                    if (uiState.isRunning) {
                                        onPauseTask()
                                    } else {
                                        onContinueTask()
                                    }
                                } else {
                                    // 警告是否终止 / 完成已存在任务并开启本任务
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
                            imageVector = if (uiState.hasTaskProcess && uiState.isRunning /* TODO: 并且是本任务 */)
                                ImageVector.vectorResource(R.drawable.baseline_pause_24)
                            else Icons.Default.PlayArrow,
                            contentDescription = "Start / Pause / Continue",
                            modifier = Modifier
                                .fillMaxSize()
//                                .size(120.dp)
                        )
                    }
                    IconButton(
                        enabled = uiState.hasTaskProcess, // TODO: 事实上还需要运行任务是此任务
                        onClick = {
                            if (true /* TODO: 非 NFC */) {
                                onFinishTask()
                            } else {
                                onTerminateTask()
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
                            imageVector = if (true /* TODO: 非 NFC */)
                                Icons.Default.Done
                            else ImageVector.vectorResource(R.drawable.baseline_stop_24),
                            contentDescription = "Finish / Terminate",
                            modifier = Modifier
                                .fillMaxSize()
//                                .size(70.dp)
                        )
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
            onFinishTask = {},
            onTerminateTask = {},
            onPauseTask = {},
            onContinueTask = {},
            uiState = TappedUiState(
                hasTaskProcess = true,
                isRunning = true,
                taskCnt = 1,
                accumulatedTime = 110
            )
        )
    }
}

@Preview
@Composable
private fun TaskDetailPausedPreview() {
    TappedTheme() {
        TaskDetail(
            onStartNewTask = {},
            onFinishTask = {},
            onTerminateTask = {},
            onPauseTask = {},
            onContinueTask = {},
            uiState = TappedUiState(
                hasTaskProcess = true,
                isRunning = false,
                taskCnt = 2,
                accumulatedTime = 130
            )
        )
    }
}

@Preview
@Composable
private fun TaskDetailNotActivePreview() {
    TappedTheme() {
        TaskDetail(
            onStartNewTask = {},
            onFinishTask = {},
            onTerminateTask = {},
            onPauseTask = {},
            onContinueTask = {},
            uiState = TappedUiState(
                hasTaskProcess = false,
                isRunning = false,
                taskCnt = 2,
                accumulatedTime = 130
            )
        )
    }
}