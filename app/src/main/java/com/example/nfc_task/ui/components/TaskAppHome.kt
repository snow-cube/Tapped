package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nfc_task.ui.components.task_list.TaskListPage
import com.example.nfc_task.ui.theme.NFCTaskTheme
import kotlinx.coroutines.launch

val screenItems = listOf(
    HomeScreen.TaskList, HomeScreen.Statistics, HomeScreen.Profile
)

@Composable
fun TaskAppHome() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ })
                // ...other drawer items
            }
        }
    ) {
        // Screen content
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(containerColor = MaterialTheme.colorScheme.surface, bottomBar = {
            BottomBar(screenItems = screenItems,
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
                })
        }) { innerPadding ->
            NavHost(
                navController,
                startDestination = HomeScreen.TaskList.route,
                Modifier.padding(innerPadding)
            ) {
                composable(HomeScreen.TaskList.route) {
                    TaskListPage(
                        onDrawerBtnClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    )
                }
                composable(HomeScreen.Statistics.route) { }
                composable(HomeScreen.Profile.route) { }
            }
        }
    }
}

@Preview
@Composable
fun TaskAppPreview() {
    NFCTaskTheme {
        TaskAppHome()
    }
}
