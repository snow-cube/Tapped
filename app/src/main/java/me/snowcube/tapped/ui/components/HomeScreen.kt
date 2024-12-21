package me.snowcube.tapped.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector
import me.snowcube.tapped.R

sealed class HomeBottomNavigationItem(
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val includedPages: List<HomePage>
) {
    data object TaskList : HomeBottomNavigationItem(
        R.string.bottom_task_list,
        Icons.Filled.CheckCircle,
        listOf(HomePage.TaskListPersonal, HomePage.TaskListTeam)
    )

    data object Statistics : HomeBottomNavigationItem(
        R.string.bottom_statistics,
        Icons.Filled.DateRange,
        listOf(HomePage.StatisticsPage)
    )

    data object Profile : HomeBottomNavigationItem(
        R.string.bottom_profile,
        Icons.Filled.AccountCircle,
        listOf(HomePage.ProfilePage)
    )
}

val homeBottomNavigationItems = listOf(
    HomeBottomNavigationItem.TaskList,
    HomeBottomNavigationItem.Statistics,
    HomeBottomNavigationItem.Profile
)

sealed class HomePage() {
    data object TaskListPersonal : HomePage()
    data object TaskListTeam : HomePage()
    data object StatisticsPage : HomePage()
    data object ProfilePage : HomePage()
}

val homePages = listOf(
    HomePage.TaskListPersonal, HomePage.TaskListTeam, HomePage.StatisticsPage, HomePage.ProfilePage
)
