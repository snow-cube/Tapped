package com.example.nfc_task.ui.components.task_list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nfc_task.R
import com.example.nfc_task.ui.components.TaskAppTopBar
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.darkGrey

enum class TaskListEnv() {
    Personal,
    Team
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListPage(
    onDrawerBtnClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            TaskAppTopBar(
                titleText = stringResource(R.string.title_task_list),
                navigationIcon = {
                    IconButton(onClick = { onDrawerBtnClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    TextSwitch(
                        currentDestinationRoute = currentDestination?.route
                            ?: TaskListEnv.Personal.name,
                        onSwitchBtnClick = {
                            navController.navigate(it)
                        }
                    )
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        NavHost(
            navController,
//            startDestination = TaskListEnv.Personal.name,
            startDestination = TaskListEnv.Team.name,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(route = TaskListEnv.Personal.name) {
                PersonalTaskList()
            }
            composable(route = TaskListEnv.Team.name) {
                TeamTaskList()
            }
        }
    }
}

@Composable
private fun TextSwitch(
    currentDestinationRoute: String,
    onSwitchBtnClick: (String) -> Unit
) {
    Surface(
//                color = MaterialTheme.colorScheme.background,
        color = Color(0x0C000000),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
//                    .width(230.dp)
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp)
        ) {
            TextSwitchBtn(
                selected = TaskListEnv.Personal.name == currentDestinationRoute,
                "个人"
            ) { onSwitchBtnClick(TaskListEnv.Personal.name) }
            TextSwitchBtn(
                selected = TaskListEnv.Team.name == currentDestinationRoute,
                "小组",
                onClick = { onSwitchBtnClick(TaskListEnv.Team.name) }
            )

        }
    }
}

@Composable
private fun TextSwitchBtn(selected: Boolean, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondary else Color(0x00000000),
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else darkGrey,
            disabledContainerColor = Color(0x00000000),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .width(70.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun TaskListPagePreview() {
    NFCTaskTheme {
        TaskListPage(
            onDrawerBtnClick = {}
        )
    }
}
