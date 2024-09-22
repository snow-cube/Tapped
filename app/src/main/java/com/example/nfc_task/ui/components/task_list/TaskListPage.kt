package com.example.nfc_task.ui.components.task_list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.nfc_task.R
import com.example.nfc_task.ui.components.TaskAppTopBar
import com.example.nfc_task.ui.theme.NFCTaskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListPage(
    onDrawerBtnClick: () -> Unit
) {
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
        TaskListBody(innerPadding)
    }
}

@Composable
fun TaskListBody(innerPadding: PaddingValues) {

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
