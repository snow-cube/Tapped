package me.snowcube.tapped.ui.components.widgets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    beginDateSelected: Long?,
    endDateSelected: Long?,
    updateDateRange: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val dateRangePickerState: DateRangePickerState =
        if (beginDateSelected == null && endDateSelected == null) {
            rememberDateRangePickerState()
        } else {
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = beginDateSelected,
                initialSelectedEndDateMillis = endDateSelected
            )
        }

    val formatter = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val beginDateSelectedFormatted = beginDateSelected?.let {
        formatter.format(Date(it))
    } ?: ""
    val endDateSelectedFormatted = endDateSelected?.let {
        formatter.format(Date(it))
    } ?: ""
    val displayDateRange: String =
        if (beginDateSelectedFormatted == "" && endDateSelectedFormatted == "") {
            ""
        } else {
            "$beginDateSelectedFormatted - $endDateSelectedFormatted"
        }

    OutlinedTextField(
        value = displayDateRange,
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
//            modifier = Modifier.width(600.dp),
            onDismissRequest = {
                onDismiss()
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    updateDateRange(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )

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
            DateRangePicker(
//                modifier = Modifier.height(500.dp),
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "选择日期范围",
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 5.dp
                        )
                    )
                },
                headline = {
                    val beginDateText = dateRangePickerState.selectedStartDateMillis?.let {
                        formatter.format(Date(it))
                    } ?: "开始日期"
                    val endDateText = dateRangePickerState.selectedEndDateMillis?.let {
                        formatter.format(Date(it))
                    } ?: "结束日期"
                    val headlineText: String = "$beginDateText - $endDateText"
                    Text(
                        headlineText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )

                },
                showModeToggle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .padding(
                        vertical = 16.dp,
                        horizontal = 0.dp
                    )
            )
        }
    }
}
