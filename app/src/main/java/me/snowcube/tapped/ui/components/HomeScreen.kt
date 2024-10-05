package me.snowcube.tapped.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector
import me.snowcube.tapped.R

sealed class HomeScreen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val includedRoutes: List<String>
) {
    data object TaskList : HomeScreen(
        "task_list",
        R.string.bottom_task_list,
        Icons.Filled.CheckCircle,
        includedRoutes = listOf(TaskListEnv.Personal.name, TaskListEnv.Team.name)
    )
    data object Statistics : HomeScreen(
        "statistics",
        R.string.bottom_statistics,
        Icons.Filled.DateRange,
        includedRoutes = listOf()
    )
    data object Profile : HomeScreen(
        "profile",
        R.string.bottom_profile,
        Icons.Filled.AccountCircle,
        includedRoutes = listOf()
    )
}

enum class TaskListEnv() {
    Personal,
    Team
}