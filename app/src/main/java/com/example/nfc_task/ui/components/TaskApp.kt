package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class TaskAppScreen {
    Home,
    TaskDetail,
    Login
}

@Composable
fun TaskApp() {
    val taskAppNavController = rememberNavController()
    NavHost(
        navController = taskAppNavController,
        startDestination = TaskAppScreen.Home.name
    ) {
        composable(route = TaskAppScreen.Home.name) {
            TaskAppHome()
        }
        composable(route = TaskAppScreen.TaskDetail.name) {
            TaskDetail()
        }
        composable(route = TaskAppScreen.Login.name) {
            LoginPage()
        }
    }

}

@Composable
fun LoginPage() {
    TODO("Not yet implemented")
}

@Composable
fun TaskDetail() {
    TODO("Not yet implemented")
}
