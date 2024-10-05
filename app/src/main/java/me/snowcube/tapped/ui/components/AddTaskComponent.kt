package me.snowcube.tapped.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.snowcube.tapped.R
import me.snowcube.tapped.ui.theme.paletteColor
import me.snowcube.tapped.models.WriteState
import me.snowcube.tapped.ui.theme.TappedTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskComponent(
    onFinishClick: (String, String) -> Unit,
    onCloseClick: () -> Unit,
    onWriteClick: () -> Unit,
    onCloseWritingClick: () -> Unit,
    writingState: WriteState,
    modifier: Modifier = Modifier
) {

    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var switchSelected by remember { mutableStateOf("NFC") }
    val taskNfcEnabled = switchSelected == "NFC"

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
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onCloseClick
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
                    selected = switchSelected,
                    btnList = listOf("NFC", "普通"),
                    onSelectedChanged = { switchSelected = it },
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    modifier = modifier
                        .width(120.dp)
                )
                IconButton(
                    onClick = {
                        val date = Date(selectedDate!!)
                        val formattedDate =
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)

                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                        cal.set(Calendar.MINUTE, selectedTime!!.minute)
                        cal.isLenient = false

                        val formattedTime =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)

                        onFinishClick(
                            taskName,
                            "$formattedDate $formattedTime"
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Finish task adding page"
                    )
                }
            }
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("任务名称") },
                shape = MaterialTheme.shapes.medium,
                trailingIcon = {
                    IconButton(
                        onClick = { taskName = "" },
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

            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("描述信息") },
                trailingIcon = {
                    IconButton(
                        onClick = { taskDescription = "" },
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

            DatePicker(
                onConfirm = {
                    selectedDate = it
                },
                onDismiss = { },
            )

            TimePicker(
                onConfirm = { time ->
                    selectedTime = time
                }, onDismiss = {

                })
        }
        OperationButton(
            text = "写入",
            enabled = taskNfcEnabled,
            modifier = modifier
                .fillMaxWidth()
        ) {
            onWriteClick()
        }
    }

    if (writingState != WriteState.Closed) {
        WritingDialog(
            writingState = writingState,
            onDismissRequest = onCloseWritingClick,
        )
    }
}

@Composable
fun WritingDialog(
    writingState: WriteState,
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
                        WriteState.Failed -> "写入失败！"
                        WriteState.Succeeded -> "写入成功！"
                        else -> "请将设备靠近 NFC 标签以写入任务"
                    },
                    textAlign = TextAlign.Center,
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
                    if (writingState == WriteState.Writing) {
                        Text("取消")
                    } else {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    var showTimePicker by remember { mutableStateOf(false) }
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true, // TODO: Enable to switch between 12 & 24 h
    )

    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var displayTime by remember { mutableStateOf("") }

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(true) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24)
    } else {
        ImageVector.vectorResource(R.drawable.baseline_access_time_filled_24)
    }

    OutlinedTextField(
        value = displayTime,
        onValueChange = { },
        label = { Text("任务时间") },
        readOnly = true,
        shape = MaterialTheme.shapes.medium,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = !showTimePicker }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_access_time_filled_24),
                    contentDescription = "Select time"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    )

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = {
                onDismiss()
                showTimePicker = false
            },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                cal.set(Calendar.MINUTE, timePickerState.minute)
                cal.isLenient = false
                displayTime = formatter.format(cal.time)

                onConfirm(timePickerState)
                showTimePicker = false
            },
            toggle = {
                IconButton(onClick = { showDial = !showDial }) {
                    Icon(
                        imageVector = toggleIcon,
                        contentDescription = "Time picker type toggle",
                    )
                }
            },
        ) {
            if (showDial) {
                TimePicker(
                    state = timePickerState,
                )
            } else {
                TimeInput(
                    state = timePickerState,
                )
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "选择时间",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val formatter = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val selectedDate = datePickerState.selectedDateMillis?.let {
        formatter.format(Date(it))
    } ?: ""

    OutlinedTextField(
        value = selectedDate,
        onValueChange = { },
        label = { Text("任务日期") },
        readOnly = true,
        shape = MaterialTheme.shapes.medium,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onDismiss()
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                    showDatePicker = false
                }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.large
        ) {
            DatePicker(state = datePickerState)
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
                onFinishClick = { _, _ ->

                },
                onCloseClick = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                writingState = WriteState.Closed,
            )
        }
    }
}

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
                onFinishClick = { _, _ ->

                },
                onCloseClick = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                writingState = WriteState.Writing,
            )
        }
    }
}

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
                onFinishClick = { _, _ ->

                },
                onCloseClick = {},
                onWriteClick = {},
                onCloseWritingClick = {},
                writingState = WriteState.Failed,
            )
        }
    }
}