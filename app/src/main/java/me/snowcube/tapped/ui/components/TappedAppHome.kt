package me.snowcube.tapped.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.snowcube.tapped.data.FakeTasksRepository
import me.snowcube.tapped.models.TappedAppHomeViewModel
import me.snowcube.tapped.models.TappedUiState
import me.snowcube.tapped.models.TaskProcessRecord
import me.snowcube.tapped.ui.components.task_list.PersonalTaskList
import me.snowcube.tapped.ui.components.task_list.StatisticsPage
import me.snowcube.tapped.ui.components.task_list.TeamTaskList
import me.snowcube.tapped.ui.components.widgets.AddTaskComponent
import me.snowcube.tapped.ui.components.widgets.BottomBar
import me.snowcube.tapped.ui.components.widgets.TappedAppTopBar
import me.snowcube.tapped.ui.components.widgets.TextSwitch
import me.snowcube.tapped.ui.theme.TappedTheme
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TappedAppHome(
    onTaskItemClick: (taskId: Long) -> Unit,
    writeTaskToNfc: (taskId: Long) -> Boolean,
    finishTaskProcess: () -> TaskProcessRecord?,
    performTaskOnce: (
        taskId: Long, taskProcessRecord: TaskProcessRecord?
    ) -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onBottomTaskControllerClick: (taskId: Long) -> Unit,
    tappedUiState: TappedUiState,
    onCloseWritingClick: () -> Unit,
    snackbarLauncher: SnackbarLauncher? = null,
    viewModel: TappedAppHomeViewModel = hiltViewModel()
) {

    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskListUiState by viewModel.taskListUiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(
            modifier = Modifier.width(330.dp)
        ) {
            Text("Drawer title", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            NavigationDrawerItem(label = { Text(text = "Drawer Item") },
                selected = false,
                onClick = { /*TODO*/ })
            // ...other drawer items
        }
    }) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        val homePagerState = rememberPagerState(pageCount = { homePages.size })

        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TappedAppTopBar(titleText = when (homePages[homePagerState.currentPage]) {
                    in HomeBottomNavigationItem.TaskList.includedPages -> "任务"
                    in HomeBottomNavigationItem.Statistics.includedPages -> "统计"
                    in HomeBottomNavigationItem.Profile.includedPages -> "账户"
                    else -> "未知"
                }, navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                }, actions = {
                    // 同时满足起点页面和终点页面都处于任务屏幕时才显示个人/小组切换按钮，防止在滑动切换页面中途经过
                    // 个人/小组页面时发生按钮选中状态的切换影响观感，特别是在刚出现/即将消失时发生跳动
                    // 但当前底部导航栏采用间隔超过一个页面时不滑动而直接跳转的方式，实际上应该不会出现抖动的情况
                    val visible =
                        homePages[homePagerState.settledPage] in HomeBottomNavigationItem.TaskList.includedPages
                                && homePages[homePagerState.targetPage] in HomeBottomNavigationItem.TaskList.includedPages

                    // FIXME: 由于动画导致切换按钮不会立即消失，实际上又再次引入了途径小组页面时会抖动切换的问题
                    AnimatedVisibility(
                        visible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val personalIndex = homePages.indexOf(HomePage.TaskListPersonal)
                        TextSwitch(
                            // 按钮选中状态仍依据 currentPage，确保切换动画的即时平滑
                            selected = if (homePagerState.currentPage <= personalIndex)
                                "个人" else "小组",
                            btnList = listOf("个人", "小组"),
                            onSelectedChanged = {
                                coroutineScope.launch {
                                    val targetPage = when (it) {
                                        "个人" -> HomePage.TaskListPersonal
                                        "小组" -> HomePage.TaskListTeam
                                        else -> HomePage.TaskListPersonal
                                    }
                                    val targetPageIndex = homePages.indexOf(targetPage)
                                    // Call scroll to on pagerState
                                    homePagerState.animateScrollToPage(targetPageIndex)
                                }
                            },
                            modifier = Modifier
                                .width(140.dp)
                                .height(34.dp)
                        )
                    }

                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                }, scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomBar(
                    navigationItems = homeBottomNavigationItems,
                    currentPage = homePages[homePagerState.currentPage],
                    onItemClick = { clickedItem ->
                        coroutineScope.launch {
                            // Call scroll to on pagerState
                            val targetPageIndex =
                                homePages.indexOf(clickedItem.includedPages[0])
                            if (abs(targetPageIndex - homePagerState.currentPage) <= 1) {
                                homePagerState.animateScrollToPage(targetPageIndex)
                            } else {
                                homePagerState.scrollToPage(targetPageIndex)
                            }
                        }
                    },
                    onAddTaskBtnClick = { showBottomSheet = true },
                    tappedUiState = tappedUiState,
                    finishTaskProcess = finishTaskProcess,
                    performTaskOnce = performTaskOnce,
                    onPauseTask = onPauseTask,
                    onContinueTask = onContinueTask,
                    onBottomTaskControllerClick = onBottomTaskControllerClick
                )
            }
        ) { innerPadding ->

            HorizontalPager(
                state = homePagerState,
                Modifier.padding(innerPadding)
            ) { pageIndex ->
                when (homePages[pageIndex]) {
                    HomePage.TaskListPersonal -> PersonalTaskList(
                        taskList = taskListUiState.taskList,
                        onTaskItemClick = onTaskItemClick
                    )

                    HomePage.TaskListTeam -> TeamTaskList(onTaskItemClick = onTaskItemClick)

                    HomePage.StatisticsPage -> StatisticsPage()

                    HomePage.ProfilePage -> {
                        Text(
                            "Profile", Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
//            windowInsets = WindowInsets(0, 0, 0, 0),
                    windowInsets = WindowInsets.statusBars, // Bottom sheet 并不避开底部导航条，而由内容避开
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                    ),
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
//            modifier = Modifier.fillMaxHeight(),
//            modifier = Modifier.height(600.dp),
                    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false }) {
                    AddTaskComponent(
                        nfcWritingState = tappedUiState.nfcWritingState,
                        quitComponent = {
                            // TODO: Reset UI states
                            // 该工作不能在组件内部进行，因为部分组件的应用场景中退出时不应重置 UI State，如修改已有任务

                            coroutineScope.launch() {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        writeTaskToNfc = writeTaskToNfc,
                        onCloseWritingClick = onCloseWritingClick,
                        editTaskUiState = viewModel.editTaskUiState,
                        updateEditTaskUiState = viewModel::updateEditTaskUiState,
                        saveTask = viewModel::saveTask,
                        snackbarLauncher = snackbarLauncher,
                        modifier = Modifier.safeDrawingPadding()
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun TaskAppPreview() {
    TappedTheme {
        TappedAppHome(
            onTaskItemClick = {},
            writeTaskToNfc = { false },
            finishTaskProcess = { null },
            performTaskOnce = { _, _ -> },
            onPauseTask = {},
            onContinueTask = {},
            onBottomTaskControllerClick = {},
            tappedUiState = TappedUiState(),
            onCloseWritingClick = {},
            viewModel = TappedAppHomeViewModel(tasksRepository = FakeTasksRepository())
        )
    }
}
