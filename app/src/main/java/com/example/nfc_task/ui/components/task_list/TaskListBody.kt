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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfc_task.data.Task
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.paletteColor
import com.example.nfc_task.ui.theme.variantsColor

@Composable
fun TaskListBody(onTaskItemClick: () -> Unit, folders: List<List<Task>>) {
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
            .clip(MaterialTheme.shapes.medium)
    ) {
        item {
            TaskFolder(
                folderName = "任务分组 1",
                themeColor = variantsColor[0],
                onTaskItemClick = onTaskItemClick,
                tasks = folders[0]
            )
        }
        item {
            TaskFolder(
                folderName = "Folder 2",
                themeColor = variantsColor[1],
                onTaskItemClick = onTaskItemClick,
                tasks = folders[1]
            )
        }
        item {
            TaskFolder(
                folderName = "Test Folder",
                themeColor = variantsColor[2],
                onTaskItemClick = onTaskItemClick,
                tasks = folders[2]
            )
        }
    }
}

/* TODO: Maybe later it will be passed to folder component the folder object directly rather than
 separate properties */
@Composable
private fun TaskFolder(
    folderName: String,
    themeColor: Color,
    onTaskItemClick: () -> Unit,
    tasks: List<Task>
) {
    var folded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
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
                    .height(54.dp)
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
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = paletteColor.invalidGrey
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
                    modifier = Modifier.padding(
                        top = 6.dp,
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    )
                ) {
                    tasks.forEach() {
                        TaskItem(
                            task = it,
                            onTaskItemClick = onTaskItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    onTaskItemClick: () -> Unit,
    task: Task
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            color = task.stateColor,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(5.dp)
                .height(60.dp)
        ) { }
        Surface(
            onClick = onTaskItemClick,
            color = MaterialTheme.colorScheme.surface,
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
                    // TODO: Using chip to instead
                    Text(
                        "${if (task.inNfcManner) "NFC" else "普通"}" +
                                " | ${if (task.isPeriod) "持续" else "即时"}" +
                                "${if (task.isRepeat) " | 重复" else ""}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        task.taskTime,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
//                HorizontalDivider()
                Text(
                    task.taskName,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onSurface,
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
        TaskListBody(onTaskItemClick = {}, folders = folders)
    }
}