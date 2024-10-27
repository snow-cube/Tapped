package me.snowcube.tapped.ui.components.task_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.ui.theme.TappedTheme


val folders2 = mutableListOf(
    mutableListOf(
        Task(
            inNfcManner = false,
            isPeriod = false,
            taskTitle = "小组任务 1",
            taskTime = "9/17 18:30",
        ),
        Task(
            inNfcManner = false,
            isPeriod = false,
            taskTitle = "测试任务",
            taskTime = "9/20 20:00",
        ),

        ),
    mutableListOf(
        Task(
            inNfcManner = true,
            isPeriod = false,
            taskTitle = "Task name example",
            taskTime = "9/17 15:00",
        ),
    ),
    mutableListOf(
        Task(
            inNfcManner = false,
            isPeriod = false,
            taskTitle = "任务不重复一次性",
            taskTime = "9/17 18:20",
        ),
        Task(
            inNfcManner = true,
            isPeriod = false,
            taskTitle = "测试任务2一次性",
            taskTime = "9/18 18:30",
        ),
        Task(
            inNfcManner = true,
            isPeriod = true,
            taskTitle = "测试任务重复执行",
            taskTime = "9/17 21:00 - 21:20",
        ),
        Task(
            inNfcManner = false,
            isPeriod = false,
            taskTitle = "测试任务3非NFC",
            taskTime = "9/20 20:00",
        )
    )
)

@Composable
fun TeamTaskList(onTaskItemClick: () -> Unit) {
    Column() {
        TeamInfoBoard()
        TaskListBody(onTaskItemClick = onTaskItemClick, folders = folders2)
    }
}

@Composable
fun TeamInfoBoard() {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 7.dp
            ) // 外边距
            .clickable { },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(15.dp) // 内边距
        ) {
            Text(
                "测试打卡小组 1",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
            ) {
                Text("管理员: User1", style = MaterialTheme.typography.bodyLarge)
                VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("已发布任务: 5", style = MaterialTheme.typography.bodyLarge)
            }
            Text("成员: 5人", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview
@Composable
fun TeamTaskListPreview() {
    TappedTheme() {
        TeamTaskList(onTaskItemClick = {})
    }
}