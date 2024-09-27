package com.example.nfc_task.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfc_task.R
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.darkGreen
import com.example.nfc_task.ui.theme.darkGrey
import com.example.nfc_task.ui.theme.darkYellow

@Composable
fun BottomBar(
    screenItems: List<HomeScreen>,
    currentScreenRoute: String,
    onItemClick: (targetRoute: String) -> Unit,
    modifier: Modifier = Modifier,
    onAddTaskBtnClick: () -> Unit,
    hasTaskProcess: Boolean = false,
    isRunning: Boolean = false,
    currentTaskTime: Int = 0,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onBottomTaskControllerClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier.padding(
            top = 9.dp, start = 10.dp, end = 10.dp, bottom = 20.dp
        )
    ) {
        if (hasTaskProcess) {
            BottomTaskController(
                modifier = modifier,
                isRunning = isRunning,
                currentTaskTime = currentTaskTime,
                onFinishTask = onFinishTask,
                onTerminateTask = onTerminateTask,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onClick = onBottomTaskControllerClick
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.fillMaxWidth()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .height(60.dp)
                    .width(230.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier.background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                ) {
                    screenItems.forEach { screen ->
                        NavBtn(
                            text = stringResource(screen.resourceId),
                            icon = screen.icon,
                            selected = currentScreenRoute == screen.route || currentScreenRoute in screen.includedRoutes
                        ) {
                            onItemClick(screen.route)
                        }
                    }
                }
            }

            FloatingActionButton(
                shape = MaterialTheme.shapes.extraLarge,
                onClick = onAddTaskBtnClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .height(52.dp)
                    .width(52.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Floating action button."
                )
            }
        }
    }
}

@Composable
private fun BottomTaskController(
    modifier: Modifier,
    isRunning: Boolean,
    currentTaskTime: Int,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onClick: () -> Unit
) {
    val taskStateText = if (isRunning) "进行中" else "暂停中"
    val formattedTime = DateUtils.formatElapsedTime(currentTaskTime.toLong())
    val stateColor = if (isRunning) darkGreen else darkYellow

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(30.dp)
            )
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.padding(
                vertical = 0.dp, horizontal = 20.dp
            )
        ) {
            Column(modifier.width(230.dp)) {
                Text(
                    "NFC · 测试任务 1",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = darkGrey
                )
                HorizontalDivider()
                Row {
                    Text(
                        "$taskStateText | $formattedTime",
                        color = stateColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 45.sp
                    )
                }
            }
            Row() {
                IconButton(
                    onClick = if (isRunning) onPauseTask else onContinueTask,
                    colors = IconButtonColors(
                        containerColor = Color(0x0C000000),
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color(0x0C000000),
                        disabledContentColor = darkGrey
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning)
                            ImageVector.vectorResource(R.drawable.baseline_pause_24) else
                            Icons.Filled.PlayArrow,
                        contentDescription = "Localized description",
                        modifier = modifier.size(30.dp)
                    )
                }
                IconButton(
                    onClick = onFinishTask, // 若为 NFC 任务，应为终止
                    colors = IconButtonColors(
                        containerColor = Color(0x0C000000),
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color(0x0C000000),
                        disabledContentColor = darkGrey
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Localized description",
                        modifier = modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBtn(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        contentPadding = PaddingValues(7.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .fillMaxHeight()
            .width(if (selected) 110.dp else 53.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier
                .size(30.dp)
                .align(Alignment.CenterVertically)
        )
        if (selected) {
            Spacer(modifier.width(7.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

    }

}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    NFCTaskTheme {
        BottomBar(
            screenItems = listOf(
                HomeScreen.TaskList, HomeScreen.Statistics, HomeScreen.Profile
            ),
            currentScreenRoute = "statistics",
            onItemClick = {},
            onAddTaskBtnClick = {},
            hasTaskProcess = true,
            onFinishTask = {},
            onTerminateTask = {},
            onPauseTask = {},
            onContinueTask = {},
            onBottomTaskControllerClick = {}
        )
    }
}