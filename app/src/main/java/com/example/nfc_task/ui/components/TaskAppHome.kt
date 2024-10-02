package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.nfc_task.R
import com.example.nfc_task.models.TaskUiState
import com.example.nfc_task.ui.components.task_list.PersonalTaskList
import com.example.nfc_task.ui.components.task_list.StatisticsPage
import com.example.nfc_task.ui.components.task_list.TeamTaskList
import com.example.nfc_task.ui.theme.NFCTaskTheme
import kotlinx.coroutines.launch

val screenItems = listOf(
    HomeScreen.TaskList, HomeScreen.Statistics, HomeScreen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAppHome(
    onTaskItemClick: () -> Unit,
    onWriteClick: () -> Unit,
    onFinishTask: () -> Unit,
    onTerminateTask: () -> Unit,
    onPauseTask: () -> Unit,
    onContinueTask: () -> Unit,
    onBottomTaskControllerClick: () -> Unit,
    onSaveNewTaskClick: (String, String) -> Unit,
    uiState: TaskUiState,
    onCloseWritingClick: () -> Unit
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Text("Drawer title", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            NavigationDrawerItem(label = { Text(text = "Drawer Item") },
                selected = false,
                onClick = { /*TODO*/ })
            // ...other drawer items
        }
    }) {
        // Screen content
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TaskAppTopBar(
                    titleText = stringResource(R.string.title_task_list),
                    navigationIcon = {
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
                    },
                    actions = {
                        if (currentDestination?.route in HomeScreen.TaskList.includedRoutes) {
                            TextSwitch(
                                selected = when (currentDestination?.route) {
                                    TaskListEnv.Personal.name -> "个人"
                                    TaskListEnv.Team.name -> "小组"
                                    else -> "个人"
                                },
                                btnList = listOf("个人", "小组"),
                                onSelectedChanged = {
                                    navController.navigate(
                                        when (it) {
                                            "个人" -> TaskListEnv.Personal.name
                                            "小组" -> TaskListEnv.Team.name
                                            else -> TaskListEnv.Personal.name
                                        }
                                    ) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                },
//                                backgroundColor = MaterialTheme.colorScheme.surfaceBright,
                                modifier = Modifier
                                    .width(140.dp)
                            )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomBar(
                    screenItems = screenItems,
                    currentScreenRoute = currentDestination?.route ?: HomeScreen.TaskList.route,
                    onItemClick = { targetRoute ->
                        navController.navigate(targetRoute) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    onAddTaskBtnClick = { showBottomSheet = true },
                    hasTaskProcess = uiState.hasTaskProcess,
                    isRunning = uiState.isRunning,
                    currentTaskTime = uiState.currentTaskTime,
                    onFinishTask = onFinishTask,
                    onTerminateTask = onTerminateTask,
                    onPauseTask = onPauseTask,
                    onContinueTask = onContinueTask,
                    onBottomTaskControllerClick = onBottomTaskControllerClick
                )
            }) { innerPadding ->
            NavHost(
                navController,
                startDestination = HomeScreen.TaskList.route,
                Modifier.padding(innerPadding)
            ) {
                navigation(
                    route = HomeScreen.TaskList.route,
                    startDestination = TaskListEnv.Personal.name
                ) {
                    composable(route = TaskListEnv.Personal.name) {
                        PersonalTaskList(onTaskItemClick = onTaskItemClick)
                    }
                    composable(route = TaskListEnv.Team.name) {
                        TeamTaskList(onTaskItemClick = onTaskItemClick)
                    }
                }
                composable(HomeScreen.Statistics.route) {
                    StatisticsPage()
                }
                composable(HomeScreen.Profile.route) { }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
//            windowInsets = WindowInsets(0, 0, 0, 0),
            windowInsets = WindowInsets.statusBars,
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
            ),
            containerColor = MaterialTheme.colorScheme.surfaceBright,
//            modifier = Modifier.fillMaxHeight(),
            modifier = Modifier.height(600.dp),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }) {
            AddTaskComponent(
                writingState = uiState.writeState,
                onCloseClick = {
                    showBottomSheet = false
                },
                onFinishClick = { taskName, taskTime ->
                    // TODO: Save task
                    onSaveNewTaskClick(
                        taskName,
                        taskTime
                    )

                    showBottomSheet = false
                },
                onWriteClick = onWriteClick,
                onCloseWritingClick = onCloseWritingClick,
                modifier = Modifier
                    .safeDrawingPadding()
            )
        }
    }
}

@Preview
@Composable
fun TaskAppPreview() {
    NFCTaskTheme {
        TaskAppHome(
            onTaskItemClick = {},
            onWriteClick = {},
            onFinishTask = {},
            onTerminateTask = {},
            onPauseTask = {},
            onContinueTask = {},
            onBottomTaskControllerClick = {},
            onSaveNewTaskClick = { _, _ ->

            },
            uiState = TaskUiState(),
            onCloseWritingClick = {}
        )
    }
}
