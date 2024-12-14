package me.snowcube.tapped.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import kotlinx.serialization.Serializable
import me.snowcube.tapped.R
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.models.RunningTaskUiState
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskDetailUiState
import me.snowcube.tapped.models.TaskDetailViewModel
import me.snowcube.tapped.models.TaskProcessRecord
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor

@Serializable
object TaskPanel

@Serializable
object EditTask

@Serializable
object TaskStatistics

@Composable
fun TaskDetail(
    onStartNewTask: (task: Task) -> Unit,
    finishTaskProcess: () -> TaskProcessRecord?, // 将持续任务的运行进程成功结束，返回运行记录
    performTaskOnce: (
        taskId: Long, taskProcessRecord: TaskProcessRecord?
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

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavHost(
        navController,
        startDestination = TaskPanel,
    ) {
        composable<TaskPanel> {
            if (uiState.task?.isContinuous == true) {
                ContinuousTaskPanel(
                    tappedUiState,
                    uiState,
                    onStartNewTask,
                    onPauseTask,
                    onContinueTask,
                    finishTaskProcess,
                    performTaskOnce
                )

            } else {
                NonContinuousTaskPanel(uiState, performTaskOnce)
            }
        }
        composable<EditTask> { }
        composable<TaskStatistics> {}
    }

}

@Composable
private fun NonContinuousTaskPanel(
    uiState: TaskDetailUiState, performTaskOnce: (Long, TaskProcessRecord?) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ConstraintLayout {
            val (majorInfo, minorInfo, controllerBar) = createRefs()

            Surface(shape = RoundedCornerShape(
                bottomStart = 32.dp,
                bottomEnd = 32.dp,
            ),
//                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceBright, modifier = Modifier
                    .constrainAs(majorInfo) {
                        top.linkTo(parent.top, margin = 0.dp)
                    }
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.75f)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .safeDrawingPadding()
                ) {
                    TaskInfo(uiState.task, textColor = MaterialTheme.colorScheme.onSurface)
                }
            }

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .constrainAs(minorInfo) {
                        top.linkTo(majorInfo.bottom, margin = 0.dp)
                    }
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.25f)
                    .safeDrawingPadding()) {
                Text("任务历史总结")
            }

            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .constrainAs(controllerBar) {
                        top.linkTo(majorInfo.bottom, margin = (-30).dp)
                    }
//                    .background(color = Color(0x10000000))
                    .fillMaxWidth()
                    .height(60.dp)) {
                Button(
                    enabled = uiState.task?.isCompleted == false, // TODO: 任务非 NFC
                    onClick = {
                        uiState.task?.let { task -> performTaskOnce(task.id, null) }
                    },
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceDim

                    ),
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.secondary)
//                        modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Complete the task",
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (uiState.task?.isCompleted == true) "已完成" else "完成",
//                            text = "TERMINATE",
//                            text = "FINISH",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinuousTaskPanel(
    tappedUiState: TappedUiState,
    taskDetailUiState: TaskDetailUiState,
    onStartNewTask: (Task) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (Long, TaskProcessRecord?) -> Unit
) {
    val isRelatedTaskHasProcess =
        tappedUiState.hasTaskProcess && tappedUiState.runningTaskUiState.task?.id == taskDetailUiState.task?.id

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

    Surface(
        color = stateColor, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ConstraintLayout {
            val (majorInfo, minorInfo, controllerBar) = createRefs()

            Surface(shape = RoundedCornerShape(
                bottomStart = 32.dp,
                bottomEnd = 32.dp,
            ),
//                tonalElevation = 2.dp,
                color = Color(0x2AFFFFFF), modifier = Modifier
                    .constrainAs(majorInfo) {
                        top.linkTo(parent.top, margin = 0.dp)
                    }
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.75f)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .safeDrawingPadding()
                ) {
                    // TODO: 圆形可点击，控制任务开始暂停
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
                            if (isRelatedTaskHasProcess) {
                                Spacer(Modifier.height(15.dp))
                                Text(
                                    formattedTime,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    TaskInfo(taskDetailUiState.task)
                }
            }

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .constrainAs(minorInfo) {
                        top.linkTo(majorInfo.bottom, margin = 0.dp)
                    }
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.25f)
                    .safeDrawingPadding()) {
                Text("任务历史总结")
            }

            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .constrainAs(controllerBar) {
                        top.linkTo(majorInfo.bottom, margin = (-30).dp)
                    }
//                    .background(color = Color(0x10000000))
                    .fillMaxWidth()
                    .height(60.dp)) {
                Button(
                    enabled = taskDetailUiState.task?.isCompleted == false, // TODO: 非 NFC 不可开启 但可暂停
                    onClick = {
                        if (!tappedUiState.hasTaskProcess) {
                            onStartNewTask(taskDetailUiState.task!!)
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
                    contentPadding = PaddingValues(10.dp),
                    modifier = if (isRelatedTaskHasProcess) Modifier
                        .height(48.dp)
                        .width(48.dp)
                    else Modifier
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceDim
                    ),
                ) {
                    Icon(
                        imageVector = if (isRelatedTaskHasProcess && tappedUiState.runningTaskUiState.isRunning) ImageVector.vectorResource(
                            R.drawable.baseline_pause_24
                        )
                        else Icons.Default.PlayArrow,
                        contentDescription = "Start / Pause / Continue",
                        modifier = Modifier.size(28.dp)
                    )
                    if (!isRelatedTaskHasProcess) {
                        Text(
//                            text = "START",
                            text = if (taskDetailUiState.task?.isCompleted == true) "已完成" else "开始",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
                if (isRelatedTaskHasProcess) {
                    Spacer(Modifier.width(20.dp))
                    Button(
                        enabled = true,
                        onClick = {
                            if (true /* TODO: 非 NFC */) {
                                taskDetailUiState.task?.let { task ->
                                    val record = finishTaskProcess()
                                    performTaskOnce(taskDetailUiState.task.id, record)
                                }
                            } else {
                                // TODO: 警告并确认是否终止进行中的持续任务进程
                                finishTaskProcess()
                            }
                        },
                        contentPadding = PaddingValues(10.dp),
                        modifier = Modifier
                            .height(48.dp),
                    ) {
                        Icon(
                            imageVector = if (true /* TODO: 非 NFC */) Icons.Default.Done
                            else ImageVector.vectorResource(R.drawable.baseline_stop_24),
                            contentDescription = "Finish / Terminate",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
//                            text = "FINISH",
//                            text = "TERMINATE",
                            text = "完成",
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskInfo(
    task: Task?,
    textColor: Color = Color.White
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "累计次数 ${task?.id}    |    累计时间 $0",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
        )
        Text(
            task?.taskTitle ?: "ERROR",
            style = MaterialTheme.typography.headlineSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
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