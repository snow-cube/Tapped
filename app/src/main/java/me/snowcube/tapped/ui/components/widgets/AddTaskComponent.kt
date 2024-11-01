package me.snowcube.tapped.ui.components.widgets

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(
                start = 13.dp,
                end = 13.dp,
                bottom = 10.dp
            )
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
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
                        .width(120.dp)
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            saveTask()
//                            sleep(5000)
                            Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show()
                            quitComponent()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Finish task adding page"
                    )
                }
            }
            OutlinedTextField(
                value = addTaskUiState.taskTitle,
                onValueChange = { updateAddTaskUiState(addTaskUiState.copy(taskTitle = it)) },
                label = { Text("任务标题") },
                shape = MaterialTheme.shapes.medium,
                trailingIcon = {
                    IconButton(
                        onClick = { updateAddTaskUiState(addTaskUiState.copy(taskTitle = "")) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear name"
                        )
                    }
                },
                singleLine = true,
                modifier = modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            Spacer(modifier.height(7.dp))
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
                modifier = modifier
                    .fillMaxWidth()
            )

            DatePickerComponent(
                beginDateSelected = addTaskUiState.beginDateSelected,
                endDateSelected = addTaskUiState.endDateSelected,
                updateDateRange = {
                    updateAddTaskUiState(
                        addTaskUiState.copy(
                            beginDateSelected = it.first,
                            endDateSelected = it.second
                        )
                    )
                },
                onDismiss = { },
            )

            TimePickerComponent(
                selectedTime = addTaskUiState.selectedTime,
                updateTime = {
                    updateAddTaskUiState(addTaskUiState.copy(selectedTime = it))
                },
                onDismiss = { }
            )
        }

        OperationButton(
            text = "写入",
            enabled = addTaskUiState.inNfcManner(),
            modifier = modifier
                .fillMaxWidth()
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
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .width(280.dp)
        ) {
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
                    modifier = Modifier
                        .size(100.dp)
                )
                Text(
                    text = when (writingState) {
                        NfcWritingState.Failed -> "写入失败！"
                        NfcWritingState.Succeeded -> "写入成功！"
                        else -> "请将设备靠近 NFC 标签以写入任务"
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    minLines = 2
                )
                Button(
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .width(160.dp)
                ) {
                    if (writingState == NfcWritingState.Writing) {
                        Text(
                            "取消",
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "关闭",
                            fontWeight = FontWeight.Bold
                        )
                    }
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
            AddTaskComponent(
                saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Closed,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {}
            )
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
            AddTaskComponent(
                saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Writing,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {}
            )
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
            AddTaskComponent(
                saveTask = {},
                quitComponent = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                nfcWritingState = NfcWritingState.Failed,
                addTaskUiState = AddTaskUiState(),
                updateAddTaskUiState = {}
            )
        }
    }
}