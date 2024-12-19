package me.snowcube.tapped.ui.components.task_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.snowcube.tapped.ui.theme.TappedTheme

@Composable
fun StatisticsPage() {
    Text(
        "Statistics",
        Modifier.fillMaxSize()
    )
}

@Preview
@Composable
fun StatisticsPagePreview() {
    TappedTheme {
        StatisticsPage()
    }
}