package com.example.nfc_task.ui.components.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfc_task.R
import com.example.nfc_task.ui.components.TaskAppTopBar
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.darkGreen
import com.example.nfc_task.ui.theme.darkGrey
import com.example.nfc_task.ui.theme.lightGrey
import com.example.nfc_task.ui.theme.mediumGrey
import com.example.nfc_task.ui.theme.normalGreen
import com.example.nfc_task.ui.theme.normalRed

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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(innerPadding)
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 0.dp
            )
            .fillMaxWidth()
    ) {
        item {
            TaskFolder(themeColor = normalGreen, folderName = "任务分组 1")

        }
        item {
            TaskFolder(themeColor = normalRed, folderName = "Folder 2")

        }
        item {
            TaskFolder(themeColor = normalGreen, folderName = "Test Folder")

        }
    }
}

@Composable
fun TaskFolder(
    folderName: String,
    themeColor: Color
) {
    var folded by remember { mutableStateOf(true) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                color = themeColor,
                width = 1.dp,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column {
            Surface(
//                shape = MaterialTheme.shapes.medium,
                color = themeColor,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(
                        vertical = 0.dp,
                        horizontal = 20.dp
                    )
                ) {
                    Text(
                        folderName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { folded = !folded },
                        colors = IconButtonColors(
                            containerColor = Color(0x00000000),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x00000000),
                            disabledContentColor = darkGrey
                        )
                    ) {
                        Icon(
                            imageVector = if (folded) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
            }
            if (!folded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(10.dp)
                ) {

                    TaskItem(
                        inNfcManner = true,
                        isPeriod = true,
                        isRepeat = false,
                        taskName = "测试任务1",
                        taskTime = "9/17 19:00 - 19:20"
                    )
                    TaskItem(
                        inNfcManner = true,
                        isPeriod = false,
                        isRepeat = false,
                        taskName = "测试任务2一次性",
                        taskTime = "9/17 18:30"
                    )
                    TaskItem(
                        inNfcManner = true,
                        isPeriod = true,
                        isRepeat = true,
                        taskName = "测试任务重复执行",
                        taskTime = "9/17 21:00 - 21:20"
                    )
                    TaskItem(
                        inNfcManner = false,
                        isPeriod = false,
                        isRepeat = false,
                        taskName = "测试任务3非NFC",
                        taskTime = "9/17 20:00"
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    inNfcManner: Boolean = false,
    isPeriod: Boolean = false,
    isRepeat: Boolean = false,
    taskName: String,
    taskTime: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            color = darkGreen,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(5.dp)
                .height(60.dp)
        ) { }
        Surface(
            color = Color(0x0A000000),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .height(60.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 15.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "${if (inNfcManner) "NFC" else "普通"}" +
                                " | ${if (isPeriod) "持续" else "即时"}" +
                                "${if (isRepeat) " | 重复" else ""}",
                        color = mediumGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        taskTime,
                        color = mediumGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
//                HorizontalDivider()
                Text(taskName, lineHeight = 30.sp, color = darkGrey)
            }
        }

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
