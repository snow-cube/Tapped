package com.example.nfc_task.ui.components.task_list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.StateColor
import com.example.nfc_task.ui.theme.ThemeColor
import com.example.nfc_task.ui.theme.darkGrey
import com.example.nfc_task.ui.theme.mediumGrey

@Composable
fun TaskListBody() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 0.dp
            )
            .fillMaxWidth()
    ) {
        item {
            TaskFolder(themeColor = ThemeColor.blue.color, folderName = "任务分组 1")
        }
        item {
            TaskFolder(themeColor = ThemeColor.yellow.color, folderName = "Folder 2")
        }
        item {
            TaskFolder(themeColor = ThemeColor.green.color, folderName = "Test Folder")
        }
    }
}

/* TODO: Maybe later it will be passed to folder component the folder object directly rather than
 separate properties */
@Composable
fun TaskFolder(
    folderName: String,
    themeColor: Color
) {
    var folded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                color = themeColor,
                width = 1.dp,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column {
            Surface(
                onClick = { folded = !folded },
//                shape = MaterialTheme.shapes.medium,
                color = themeColor,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(
                        vertical = 0.dp,
                        horizontal = 20.dp
                    )
                ) {
                    Text(
                        folderName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { folded = !folded },
                        colors = IconButtonColors(
                            containerColor = Color(0x00000000),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x00000000),
                            disabledContentColor = darkGrey
                        )
                    ) {
                        Icon(
                            imageVector = if (folded) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
            }
            if (!folded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(10.dp)
                ) {

                    TaskItem(
                        inNfcManner = true,
                        isPeriod = true,
                        isRepeat = false,
                        taskName = "测试任务1",
                        taskTime = "9/17 19:00 - 19:20",
                        stateColor = StateColor.unsafe.color
                    )
                    TaskItem(
                        inNfcManner = true,
                        isPeriod = false,
                        isRepeat = false,
                        taskName = "测试任务2一次性",
                        taskTime = "9/18 18:30",
                        stateColor = StateColor.normal.color
                    )
                    TaskItem(
                        inNfcManner = true,
                        isPeriod = true,
                        isRepeat = true,
                        taskName = "测试任务重复执行",
                        taskTime = "9/17 21:00 - 21:20",
                        stateColor = StateColor.warning.color
                    )
                    TaskItem(
                        inNfcManner = false,
                        isPeriod = false,
                        isRepeat = false,
                        taskName = "测试任务3非NFC",
                        taskTime = "9/20 20:00",
                        stateColor = StateColor.safe.color
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    inNfcManner: Boolean = false,
    isPeriod: Boolean = false,
    isRepeat: Boolean = false,
    taskName: String,
    taskTime: String,
    stateColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            color = stateColor,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(5.dp)
                .height(60.dp)
        ) { }
        Surface(
            color = Color(0x0A000000),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .height(60.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        vertical = 4.dp,
                        horizontal = 15.dp
                    )

            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "${if (inNfcManner) "NFC" else "普通"}" +
                                " | ${if (isPeriod) "持续" else "即时"}" +
                                "${if (isRepeat) " | 重复" else ""}",
                        color = mediumGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        taskTime,
                        color = mediumGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
//                HorizontalDivider()
                Text(
                    taskName,
                    lineHeight = 30.sp,
                    color = darkGrey,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskListBodyPreview() {
    NFCTaskTheme {
        TaskListBody()
    }
}