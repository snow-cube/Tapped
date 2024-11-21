package me.snowcube.tapped.ui.components.task_list

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.ui.theme.StateColor
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor
import me.snowcube.tapped.ui.theme.variantsColor

@Composable
fun TaskListBody(
    onTaskItemClick: (taskId: Int) -> Unit, taskList: List<List<Task>>
) {
    CommonView(taskList[0], onTaskItemClick)
//    FolderView(taskList, onTaskItemClick)
}

@Composable
private fun FolderView(
    taskList: List<List<Task>>, onTaskItemClick: (taskId: Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier
            .padding(
                top = 5.dp, start = 10.dp, end = 10.dp, bottom = 0.dp
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        items(items = taskList) {
            TaskFolder(
                folderName = "任务分组 1",
                themeColor = variantsColor[0],
                onTaskItemClick = onTaskItemClick,
                tasks = it
            )
        }
    }
}

@Composable
private fun CommonView(
    taskList: List<Task>, onTaskItemClick: (taskId: Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 5.dp, start = 10.dp, end = 10.dp, bottom = 0.dp
            )
    ) {
        if (taskList.isEmpty()) {
            Text(
                text = "无任务",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier
//                    .fillMaxHeight()
                    .padding(
                        top = 12.dp, start = 10.dp, end = 10.dp, bottom = 12.dp
                    )
            ) {
                items(taskList) {
                    TaskItem(
                        task = it, onTaskItemClick = onTaskItemClick
                    )
                }
            }
        }
    }
}

/* TODO: Maybe later it will be passed to folder component the folder object directly rather than
 separate properties */
@Composable
private fun TaskFolder(
    folderName: String, themeColor: Color, onTaskItemClick: (taskId: Int) -> Unit, tasks: List<Task>
) {
//    val initialFolded = tasks.isEmpty()
    val initialFolded = false
    var folded by remember { mutableStateOf(initialFolded) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                color = themeColor, width = 1.dp, shape = MaterialTheme.shapes.medium
            )
    ) {
        Column {
            Surface(
                onClick = { folded = !folded },
//                shape = MaterialTheme.shapes.medium,
                color = themeColor, modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(
                        vertical = 0.dp, horizontal = 5.dp
                    )
                ) {
                    IconButton(
                        onClick = {}, colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = paletteColor.invalidGrey
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Text(
                        folderName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { folded = !folded }, colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = paletteColor.invalidGrey
                        )
                    ) {
                        Icon(
                            imageVector = if (folded) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            if (!folded) {
                if (tasks.isEmpty()) {
                    Text(
                        text = "无任务",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(
                            top = 8.dp, start = 10.dp, end = 10.dp, bottom = 12.dp
                        )
                    ) {
                        tasks.forEach() {
                            TaskItem(
                                task = it, onTaskItemClick = onTaskItemClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    onTaskItemClick: (taskId: Int) -> Unit, task: Task
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Surface(
            color = StateColor.entries[task.id % 4].color,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(6.dp)
//                .width(54.dp)
                .height(54.dp)
        ) { }
        Surface(
            onClick = { onTaskItemClick(task.id) },
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(54.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        vertical = 4.dp, horizontal = 15.dp
                    )

            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // TODO: Using chip to instead
                    Text(
                        (if (task.inNfcManner) "NFC" else "普通") + " | ${if (task.isContinuous) "持续" else "即时"}" + " | 重复",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
//                        task.taskTime,
                        "20:39 2024/11/1",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
//                HorizontalDivider()
                Text(
                    task.taskTitle,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskListBodyPreview() {
    TappedTheme() {
        TaskListBody(
            onTaskItemClick = {}, taskList = listOf(
                listOf(
                    Task(
                        1,
                        "Task name",
                        "Task description...",
                        "",
                        inNfcManner = true,
                        isContinuous = true,
                        isCompleted = false,
                        isRepetitive = true,
                    ), Task(
                        2,
                        "Task name 2",
                        "Task description...",
                        "",
                        inNfcManner = false,
                        isContinuous = true,
                        isCompleted = true,
                        isRepetitive = false,
                    )
                )
            )
        )
    }
}