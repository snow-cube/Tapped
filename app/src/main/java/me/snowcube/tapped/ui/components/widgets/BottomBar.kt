package me.snowcube.tapped.ui.components.widgets

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.snowcube.tapped.R
import me.snowcube.tapped.models.RunningTaskUiState
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskProcessRecord
import me.snowcube.tapped.ui.components.HomeBottomNavigationItem
import me.snowcube.tapped.ui.components.HomePage
import me.snowcube.tapped.ui.components.homeBottomNavigationItems
import me.snowcube.tapped.ui.components.homePages
import me.snowcube.tapped.ui.theme.TappedTheme
import me.snowcube.tapped.ui.theme.paletteColor
import me.snowcube.tapped.ui.utils.DisposableEffectWithLifecycle
import kotlin.math.roundToInt

@Composable
fun BottomBar(
    navigationItems: List<HomeBottomNavigationItem>,
    currentPage: HomePage,
    onItemClick: (clickedItem: HomeBottomNavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    onAddTaskBtnClick: () -> Unit,
    tappedUiState: TappedUiState,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (
        taskId: Long, taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onBottomTaskControllerClick: (taskId: Long) -> Unit,

    ) {

    var resumed by remember { mutableStateOf(false) }

    DisposableEffectWithLifecycle(onResume = { resumed = true })

    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp), modifier = modifier
//            .background(color = MaterialTheme.colorScheme.surface)
            .padding(
                top = 7.dp, start = 10.dp, end = 10.dp, bottom = 5.dp
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        AnimatedVisibility(
            resumed && tappedUiState.hasTaskProcess,
            enter = slideInVertically(
                initialOffsetY = { it }, animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + shrinkVertically() + fadeOut()
        ) {
            BottomTaskController(modifier = modifier,
                runningTaskUiState = tappedUiState.runningTaskUiState,
                finishTaskProcess = finishTaskProcess,
                performTaskOnce = performTaskOnce,
                onPauseTask = onPauseTask,
                onContinueTask = onContinueTask,
                onClick = {
                    tappedUiState.runningTaskUiState.task?.let {
                        onBottomTaskControllerClick(
                            it.id
                        )
                    }
                })
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
                    .height(56.dp)
                    .width(230.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Box() {
                    // 前面每存在一个未选中 button 的偏移量， 包括 48.dp width 和 8.dp spacing
                    val unselectedPxToOffset = with(LocalDensity.current) {
                        56.dp.toPx().roundToInt()
                    }

                    // 选中 button 需要额外的偏移量 (110 - 48 = 62).dp
                    val selectedPxToOffsetExtra = with(LocalDensity.current) {
                        62.dp.toPx().roundToInt()
                    }

                    // 寻找被选中 button 下标
                    var selectedIndex = 0
                    navigationItems.forEachIndexed { index, item ->
                        if (currentPage in item.includedPages) selectedIndex = index
                    }

                    val highlightOffset by animateIntOffsetAsState(
                        targetValue = IntOffset(
                            unselectedPxToOffset * selectedIndex,
                            0
                        ), label = "highlight offset"
                    )

                    Box(
                        modifier = Modifier
                            .offset { highlightOffset }
                            .width(110.dp)
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                    )

                    navigationItems.forEachIndexed { index, item ->
                        val offset by animateIntOffsetAsState(
                            targetValue = IntOffset(
                                unselectedPxToOffset * index + if (selectedIndex < index) selectedPxToOffsetExtra else 0,
                                0
                            ), label = "button offset"
                        )

                        NavBtn(text = stringResource(item.resourceId),
                            icon = item.icon,
                            selected = currentPage in item.includedPages,
                            modifier = Modifier.offset { offset }) {
                            onItemClick(item)
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
                    .height(48.dp)
                    .width(48.dp)
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
    runningTaskUiState: RunningTaskUiState,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (
        taskId: Long, taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onClick: () -> Unit
) {
    val taskStateText = if (runningTaskUiState.isRunning) "进行中" else "暂停中"
    val formattedTime = DateUtils.formatElapsedTime(runningTaskUiState.currentTaskTime.toLong())
    val stateColor =
        if (runningTaskUiState.isRunning) paletteColor.darkGreen else paletteColor.darkYellow

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(30.dp)
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
            Column(
                modifier.width(230.dp), verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    "${if (runningTaskUiState.task?.inNfcManner == true) "NFC" else "普通"} · ${runningTaskUiState.task?.taskTitle}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$taskStateText | ",
                        color = stateColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 45.sp
                    )
                    Text(
                        formattedTime,
                        color = stateColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 45.sp
                    )
                }
            }
            Row() {
                IconButton(
                    onClick = if (runningTaskUiState.isRunning) onPauseTask else onContinueTask,
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.Red
                    )
                ) {
                    Icon(
                        imageVector = if (runningTaskUiState.isRunning) ImageVector.vectorResource(R.drawable.baseline_pause_24) else Icons.Filled.PlayArrow,
                        contentDescription = "Localized description",
                        modifier = modifier.size(30.dp)
                    )
                }
                IconButton(
                    onClick = {
                        if (true /* TODO: 非 NFC */) {
                            val record = finishTaskProcess()
                            runningTaskUiState.task?.let { performTaskOnce(it.id, record) }
                        } else {
                            // TODO: 警告并确认是否终止进行中的持续任务进程
                            finishTaskProcess()
                        }
                    }, colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.Red
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
        contentPadding = PaddingValues(11.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .fillMaxHeight()
            .width(if (selected) 110.dp else 48.dp) // 48 = 26 + 11 * 2
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.CenterVertically)
        )

        AnimatedVisibility(
            selected,
            enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(62.dp) // 62 = 110 (width of nav button) - 48 (height of nav button)
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    TappedTheme() {
        BottomBar(
            navigationItems = homeBottomNavigationItems,
            currentPage = homePages[0],
            onItemClick = {},
            onAddTaskBtnClick = {},
            finishTaskProcess = { null },
            performTaskOnce = { _, _ -> },
            onPauseTask = {},
            onContinueTask = {},
            onBottomTaskControllerClick = {},
            tappedUiState = TappedUiState()
        )
    }
}