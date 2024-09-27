package com.example.nfc_task.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nfc_task.R
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.lightGrey
import com.example.nfc_task.ui.theme.mediumGrey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskComponent(
    onFinishClick: (
        String,
        String
    ) -> Unit,
    onCloseClick: () -> Unit,
    onWriteClick: () -> Unit
) {

    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var switchSelected by remember { mutableStateOf("NFC") }
    val taskNfcEnabled = switchSelected == "NFC"

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(
                horizontal = 13.dp
            )
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
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
                    modifier = Modifier
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
                            contentColor = mediumGrey
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear name"
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
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
                            contentColor = mediumGrey
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
                modifier = Modifier
                    .fillMaxWidth()
            )

            DatePicker(
                onConfirm = {
                    selectedDate = it
                },
                onDismiss = { }
            )

            TimePicker(onConfirm = { time ->
                selectedTime = time
            }, onDismiss = {

            })
        }
        OperationButton(
            text = "写入",
            enabled = taskNfcEnabled,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            onWriteClick()
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

//@Composable
//private fun ConfirmButton(
//    modifier: Modifier = Modifier,
//    text: String = "确认",
//    enabled: Boolean = true,
//    colors: ButtonColors = ButtonDefaults.buttonColors(
//        containerColor = MaterialTheme.colorScheme.primary,
//        contentColor = MaterialTheme.colorScheme.onPrimary,
//        disabledContainerColor = Color(0x5A000000),
//        disabledContentColor = Color.White
//    ),
//    onClick: () -> Unit,
//) {
//    OperationButton(
//        modifier = modifier, text = text, enabled = enabled, colors = colors, onClick = onClick
//    )
//}
//
//@Composable
//private fun CancelButton(
//    modifier: Modifier = Modifier,
//    text: String = "取消",
//    enabled: Boolean = true,
//    colors: ButtonColors = ButtonDefaults.buttonColors(
//        containerColor = Color(0x1A000000),
//        contentColor = Color.Black,
//        disabledContainerColor = Color(0x5A000000),
//        disabledContentColor = Color.White
//    ),
//    onClick: () -> Unit,
//) {
//    OperationButton(
//        modifier = modifier, text = text, enabled = enabled, colors = colors, onClick = onClick
//    )
//}

@Composable
private fun OperationButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = Color(0x0A000000),
        disabledContentColor = mediumGrey
    ),
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
    NFCTaskTheme {
        Surface(
            color = Color.White, modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            AddTaskComponent(
                onCloseClick = {},
                onFinishClick = { _, _ ->

                },
                onWriteClick = {}
            )
        }
    }
}