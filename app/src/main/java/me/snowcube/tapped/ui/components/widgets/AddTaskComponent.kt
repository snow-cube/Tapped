package me.snowcube.tapped.ui.components.widgets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import me.snowcube.tapped.R
import me.snowcube.tapped.models.EditTaskUiState
import me.snowcube.tapped.models.NfcWritingState
import me.snowcube.tapped.models.TaskDetailsUiState
import me.snowcube.tapped.models.inNfcManner
import me.snowcube.tapped.ui.components.SnackbarLauncher
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor
import me.snowcube.tapped.ui.theme.shapes

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskComponent(
    modifier: Modifier = Modifier,
    saveTask: suspend () -> Long,
    quitComponent: () -> Unit,
    writeTaskToNfc: (taskId: Long) -> Boolean,
    onCloseWritingClick: () -> Unit,
    nfcWritingState: NfcWritingState,
    editTaskUiState: EditTaskUiState,
    updateEditTaskUiState: (TaskDetailsUiState) -> Unit,
    snackbarLauncher: SnackbarLauncher? = null,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showRepetitionConfig by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween, modifier = modifier
            .padding(
                start = 13.dp, end = 13.dp, bottom = 10.dp
            )
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = quitComponent
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close task adding page"
                    )
                }
//                Text(
//                    "添加任务",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
                TextSwitch(
                    selected = editTaskUiState.taskDetails.switchSelected,
                    btnList = listOf("NFC", "普通"),
                    onSelectedChanged = {
                        updateEditTaskUiState(
                            editTaskUiState.taskDetails.copy(
                                switchSelected = it
                            )
                        )
                    },
                    roundedCorner = true,
                    modifier = modifier
                        .width(120.dp)
                        .height(36.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.width(140.dp)
//                        .background(color = Color.Gray)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val taskId = saveTask()
                                if (taskId == -1L) {
                                    snackbarLauncher?.launch("保存失败")
                                    return@launch
                                }
//                            sleep(5000)
                                if (editTaskUiState.taskDetails.inNfcManner()) {
                                    if (!writeTaskToNfc(taskId)) {
                                        snackbarLauncher?.launch("写入错误")

                                        // TODO: 处理写入识别的情况 如删除已插入的任务

                                        return@launch
                                    }
                                } else {
                                    quitComponent()
                                }

//                                quitComponent()
                            }
                        },
                        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 15.dp),
                        modifier = modifier.height(36.dp)
                    ) {
                        Text(
                            if (editTaskUiState.taskDetails.inNfcManner()) "保存并写入" else "保存",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.animateContentSize()
                        )
                        Spacer(modifier = modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Finish task adding page",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            OutlinedInput(
                value = editTaskUiState.taskDetails.taskTitle,
                onValueChange = { updateEditTaskUiState(editTaskUiState.taskDetails.copy(taskTitle = it)) },
                trailingIcon = {
                    IconButton(
                        onClick = { updateEditTaskUiState(editTaskUiState.taskDetails.copy(taskTitle = "")) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear, contentDescription = "Clear name"
                        )
                    }
                },
                label = "任务标题",
                modifier = modifier.fillMaxWidth()
            )

            TextField(
                value = editTaskUiState.taskDetails.taskDescription,
                onValueChange = {
                    updateEditTaskUiState(
                        editTaskUiState.taskDetails.copy(
                            taskDescription = it
                        )
                    )
                },
                label = { Text("描述信息") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            updateEditTaskUiState(
                                editTaskUiState.taskDetails.copy(
                                    taskDescription = ""
                                )
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear description"
                        )
                    }
                },
                minLines = 2,
                maxLines = 5,
                modifier = modifier.fillMaxWidth()
            )

            SettingItemSwitch(
                label = "持续任务",
                checked = editTaskUiState.taskDetails.isContinuous,
                onCheckedChange = {
                    updateEditTaskUiState(editTaskUiState.taskDetails.copy(isContinuous = it))
                },
                modifier = modifier,
            )

            // TODO: 支持选择“早于”、“迟于”、“宽限时长”
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePickerComponent(
                    label = "开始时间",
                    selectedTime = editTaskUiState.taskDetails.selectedStartTime,
                    updateTime = {
                        updateEditTaskUiState(editTaskUiState.taskDetails.copy(selectedStartTime = it))
                    },
                    onDismiss = { },
                    modifier = Modifier.weight(1f)
                )
                TimePickerComponent(
                    label = "结束时间",
                    selectedTime = editTaskUiState.taskDetails.selectedEndTime,
                    updateTime = {
                        updateEditTaskUiState(editTaskUiState.taskDetails.copy(selectedEndTime = it))
                    },
                    onDismiss = { },
                    modifier = Modifier.weight(1f)
                )
            }

            // TODO: 支持仅结束日期，无开始日期
            DatePickerComponent(
                beginDateSelected = editTaskUiState.taskDetails.beginDateSelected,
                endDateSelected = editTaskUiState.taskDetails.endDateSelected,
                updateDateRange = {
                    updateEditTaskUiState(
                        editTaskUiState.taskDetails.copy(
                            beginDateSelected = it.first, endDateSelected = it.second
                        )
                    )
                },
                onDismiss = { },
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SettingItemMore(
                    label = "重复设置",
                    info = "<重复规则简介>",
                    onClick = { showRepetitionConfig = !showRepetitionConfig },
                    modifier = modifier.height(44.dp)
                )
                SettingItemMore(
                    label = "更多设置",
                    onClick = { snackbarLauncher?.launch("Not implemented") },
                    modifier = modifier.height(44.dp)
                )
            }
        }
//        OperationButton(
//            text = "写入",
//            enabled = addTaskUiState.inNfcManner(),
//            modifier = modifier.fillMaxWidth()
//        ) {
//            onWriteClick()
//        }
    }

    if (showRepetitionConfig) {
        ConfigDialog(
            title = "重复设置",
            showConfirm = false,
            onDismiss = { showRepetitionConfig = false },
            modifier = modifier.width(350.dp)
        ) {
            SettingItemSwitch(
                label = "重复",
                checked = editTaskUiState.taskDetails.isRepetitive,
                onCheckedChange = {
                    updateEditTaskUiState(editTaskUiState.taskDetails.copy(isRepetitive = it))
                },
            )
        }
    }

    if (nfcWritingState != NfcWritingState.Closed) {
        WritingDialog(
            writingState = nfcWritingState,
            onDismissRequest = {
                val writingResult = nfcWritingState
                onCloseWritingClick()
                if (writingResult == NfcWritingState.Succeeded) {
                    quitComponent()
                } else if (writingResult == NfcWritingState.Failed) {
                    // TODO: 处理写入识别的情况 如删除已插入的任务
                }
            },
        )
    }
}

@Composable
private fun SettingItemSwitch(
    label: String,
    checked: Boolean,
    surfaceClickable: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = surfaceClickable, onClick = { onCheckedChange?.invoke(!checked) })
            .padding(vertical = 0.dp, horizontal = 8.dp)

    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Switch(
            checked = checked, onCheckedChange = onCheckedChange, modifier = modifier.height(40.dp)
        )
    }
}

@Composable
private fun SettingItemMore(
    label: String, onClick: () -> Unit, info: String = "", modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 8.dp),
//        contentPadding = PaddingValues(start = 5.dp, top = 0.dp, bottom = 0.dp, end = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = paletteColor.invalidGrey
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
    ) {
        Text(
            label, style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            info,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier.width(10.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = label
        )
    }
}

@Composable
fun ConfigDialog(
    title: String,
    showConfirm: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
    toggle: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )

                content()

                Row(
                    modifier = modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text(if (showConfirm) "Cancel" else "收起") }
                    if (showConfirm) TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

@Composable
fun WritingDialog(
    writingState: NfcWritingState,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ), shape = MaterialTheme.shapes.large, modifier = Modifier.width(280.dp)
        ) {
            Column {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "将任务写入 NFC 标签",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.nfc),
                        contentDescription = "NFC Tag",
                        modifier = Modifier.size(100.dp)
                    )
                    Text(
                        text = when (writingState) {
                            NfcWritingState.Failed -> "写入失败！"
                            NfcWritingState.Succeeded -> "写入成功！"
                            else -> "请将设备靠近 NFC 标签以写入任务"
                        }, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, minLines = 2
                    )
                }
                Button(
                    onClick = onDismissRequest, colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    ), shape = RectangleShape, modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        if (writingState == NfcWritingState.Writing) "取消" else "关闭",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun OperationButton(
    text: String,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = paletteColor.invalidGrey,
        disabledContentColor = Color.White
    ),
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick, enabled = enabled, colors = colors, modifier = modifier.height(48.dp)
    ) {
        Text(
            text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AddTaskComponentPreview() {
    TappedTheme() {
        Surface(
            color = Color.White, modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            AddTaskComponent(saveTask = { -1 },
                quitComponent = {},
                writeTaskToNfc = { false },
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Closed,
                editTaskUiState = EditTaskUiState(),
                updateEditTaskUiState = {})
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AddTaskComponentWritingPreview() {
    TappedTheme() {
        Surface(
            color = Color.White, modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            AddTaskComponent(saveTask = { -1 },
                quitComponent = {},
                writeTaskToNfc = { false },
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Writing,
                editTaskUiState = EditTaskUiState(),
                updateEditTaskUiState = {})
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AddTaskComponentFailedPreview() {
    TappedTheme() {
        Surface(
            color = Color.White, modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            AddTaskComponent(saveTask = { -1 },
                quitComponent = {},
                writeTaskToNfc = { false },
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Failed,
                editTaskUiState = EditTaskUiState(),
                updateEditTaskUiState = {})
        }
    }
}