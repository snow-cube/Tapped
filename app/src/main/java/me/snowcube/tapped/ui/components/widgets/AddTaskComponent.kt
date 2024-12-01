package me.snowcube.tapped.ui.components.widgets

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.snowcube.tapped.R
import me.snowcube.tapped.models.AddTaskUiState
import me.snowcube.tapped.models.NfcWritingState
import me.snowcube.tapped.models.inNfcManner
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor
import java.lang.Thread.sleep

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskComponent(
    modifier: Modifier = Modifier,
    saveTask: suspend () -> Unit,
    quitComponent: () -> Unit,
    onWriteClick: () -> Unit,
    onCloseWritingClick: () -> Unit,
    nfcWritingState: NfcWritingState,
    addTaskUiState: AddTaskUiState,
    updateAddTaskUiState: (AddTaskUiState) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                Text(
                    "添加任务",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextSwitch(
                    selected = addTaskUiState.switchSelected,
                    btnList = listOf("NFC", "普通"),
                    onSelectedChanged = { updateAddTaskUiState(addTaskUiState.copy(switchSelected = it)) },
                    modifier = modifier
                        .width(110.dp)
                        .height(32.dp)
                )
                IconButton(onClick = {
                    coroutineScope.launch {
                        saveTask()
//                            sleep(5000)
                        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
                        quitComponent()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Finish task adding page"
                    )
                }
            }

            OutlinedInput(
                value = addTaskUiState.taskTitle,
                onValueChange = { updateAddTaskUiState(addTaskUiState.copy(taskTitle = it)) },
                trailingIcon = {
                    IconButton(
                        onClick = { updateAddTaskUiState(addTaskUiState.copy(taskTitle = "")) },
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
                value = addTaskUiState.taskDescription,
                onValueChange = { updateAddTaskUiState(addTaskUiState.copy(taskDescription = it)) },
                label = { Text("描述信息") },
                trailingIcon = {
                    IconButton(
                        onClick = { updateAddTaskUiState(addTaskUiState.copy(taskDescription = "")) },
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("持续任务")
                Switch(
                    checked = addTaskUiState.isContinuous,
                    onCheckedChange = {
                        updateAddTaskUiState(addTaskUiState.copy(isContinuous = it))
                    },
                )
            }

            // TODO: 支持选择“早于”、“迟于”、“宽限时长”
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePickerComponent(
                    label = "开始时间",
                    selectedTime = addTaskUiState.selectedStartTime,
                    updateTime = {
                        updateAddTaskUiState(addTaskUiState.copy(selectedStartTime = it))
                    },
                    onDismiss = { },
                    modifier = Modifier.weight(1f)
                )
                TimePickerComponent(
                    label = "结束时间",
                    selectedTime = addTaskUiState.selectedEndTime,
                    updateTime = {
                        updateAddTaskUiState(addTaskUiState.copy(selectedEndTime = it))
                    },
                    onDismiss = { },
                    modifier = Modifier.weight(1f)
                )
            }

            // TODO: 支持仅结束日期，无开始日期
            DatePickerComponent(
                beginDateSelected = addTaskUiState.beginDateSelected,
                endDateSelected = addTaskUiState.endDateSelected,
                updateDateRange = {
                    updateAddTaskUiState(
                        addTaskUiState.copy(
                            beginDateSelected = it.first, endDateSelected = it.second
                        )
                    )
                },
                onDismiss = { },
            )
        }

        OperationButton(
            text = "写入",
            enabled = addTaskUiState.inNfcManner(),
            modifier = modifier.fillMaxWidth()
        ) {
            onWriteClick()
        }
    }

    if (nfcWritingState != NfcWritingState.Closed) {
        WritingDialog(
            writingState = nfcWritingState,
            onDismissRequest = onCloseWritingClick,
        )
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
                    onClick = { onDismissRequest() }, colors = ButtonDefaults.textButtonColors(
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
                .height(500.dp)
        ) {
            AddTaskComponent(saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Closed,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {})
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
                .height(500.dp)
        ) {
            AddTaskComponent(saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Writing,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {})
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
                .height(500.dp)
        ) {
            AddTaskComponent(saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Failed,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {})
        }
    }
}