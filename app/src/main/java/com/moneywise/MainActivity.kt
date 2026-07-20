package com.moneywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moneywise.ui.screens.*
import com.moneywise.ui.theme.MoneyWiseTheme
import com.moneywise.viewmodel.SalaryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyWiseTheme {
                MoneyWiseApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Salary : Screen("salary", "Salaris", Icons.Default.Payments)
    data object WorkTime : Screen("worktime", "Werktijd", Icons.Default.Timer)
    data object Savings : Screen("savings", "Sparen", Icons.Default.Savings)
    data object Investment : Screen("investment", "Beleggen", Icons.Default.TrendingUp)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Salary,
    Screen.WorkTime,
    Screen.Savings,
    Screen.Investment,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyWiseApp() {
    val navController = rememberNavController()
    val viewModel: SalaryViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable(Screen.Salary.route) {
                SalaryInputScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(Screen.WorkTime.route) {
                WorkTimeScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(Screen.Savings.route) {
                SavingsScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(Screen.Investment.route) {
                InvestmentScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}
