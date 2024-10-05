package me.snowcube.tapped.ui.components.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.snowcube.tapped.ui.theme.TappedTheme

@Composable
fun StatisticsPage() {
    StatisticsContent(
    )
}

@Composable
private fun StatisticsContent(
    modifier: Modifier = Modifier
) {
    val commonModifier = modifier
        .fillMaxWidth()
        .padding(all = 10.dp)

    Column(
        commonModifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(5.dp)
    ) {
        StatisticItem("任务总数", "9", Color(0xFF2ECC71))
        StatisticItem("参加小组", "1", Color(0xFFFF6347))
        StatisticItem("日均打卡时长", "7 min", Color(0xFF8A2BE2))
        StatisticItem("本周打卡时长", "43 min", Color(0xFFFFC947))
        StatisticItem("平均单次打卡时长", "1 min", Color(0xFF03A9F4))
        StatisticItem("任务完成率", "82.7%", Color(0xFFEE82EE))
    }
}

@Composable
private fun StatisticItem(
    itemTitle: String,
    itemValue: String,
    barColor: Color,
) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .width(7.dp)
                .height(50.dp)
                .background(barColor)
        )
        Row(
            modifier = Modifier
                .height(50.dp)
                .background(Color(0x05000000))
                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
        ) {
//      Text(stringResource(id = R.string.statistics_active_tasks, activeTasksPercent))
//      Text(stringResource(id = R.string.statistics_completed_tasks, completedTasksPercent))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                itemTitle,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(2f)
                    .wrapContentSize(Alignment.CenterStart)
            )
            Text(
                itemValue,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentSize(Alignment.CenterEnd)
            )
        }
    }
}

@Preview
@Composable
fun StatisticsPagePreview() {
    TappedTheme {
        Surface(

        ) { StatisticsPage()}

    }
}