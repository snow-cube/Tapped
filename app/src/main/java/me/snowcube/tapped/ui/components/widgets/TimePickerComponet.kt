package me.snowcube.tapped.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.snowcube.tapped.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    selectedTime: Pair<Int, Int>?,
    updateTime: (Pair<Int, Int>) -> Unit,
    onDismiss: () -> Unit,
) {

    var showTimePicker by remember { mutableStateOf(false) }
    val currentTime = Calendar.getInstance()

    val timePickerState: TimePickerState =
        if (selectedTime == null) {
            rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = false, // TODO: Enable to switch between 12 & 24 h
            )
        } else {
            rememberTimePickerState(
                initialHour = selectedTime.first,
                initialMinute = selectedTime.second,
                is24Hour = false, // TODO: Enable to switch between 12 & 24 h
            )
        }

    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var displayTime = ""
    // 文本框的显示时间应根据外部 UI 状态而非本组件的 inner 状态
    if (selectedTime != null) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, selectedTime.first)
        cal.set(Calendar.MINUTE, selectedTime.second)
        cal.isLenient = false
        displayTime = formatter.format(cal.time)
    }

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
                updateTime(
                    Pair(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                )

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