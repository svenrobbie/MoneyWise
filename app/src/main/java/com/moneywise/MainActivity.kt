package com.moneywise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.moneywise.ui.screens.*
import com.moneywise.ui.theme.MoneyWiseTheme
import com.moneywise.viewmodel.PortfolioViewModel
import com.moneywise.viewmodel.SalaryViewModel
import com.moneywise.worker.InvestmentReminderWorker
import com.moneywise.worker.NotificationHelper
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        scheduleInvestmentReminder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        enableEdgeToEdge()
        setContent {
            val viewModel: SalaryViewModel = viewModel()
            val darkModeOverride by viewModel.darkModeOverride.collectAsState()
            MoneyWiseTheme(darkThemeOverride = darkModeOverride) {
                MoneyWiseApp()
            }
        }
    }

    private fun scheduleInvestmentReminder() {
        val workRequest = PeriodicWorkRequestBuilder<InvestmentReminderWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "investment_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Salary : Screen("salary", "Salaris", Icons.Default.Payments)
    data object WorkTime : Screen("worktime", "Werktijd", Icons.Default.Timer)
    data object Portfolio : Screen("portfolio", "Portefeuille", Icons.Default.TrendingUp)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Salary,
    Screen.WorkTime,
    Screen.Portfolio,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyWiseApp() {
    val navController = rememberNavController()
    val viewModel: SalaryViewModel = viewModel()
    val portfolioViewModel: PortfolioViewModel = viewModel()
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
                    viewModel = viewModel,
                    portfolioViewModel = portfolioViewModel
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
            composable(Screen.Portfolio.route) {
                PortfolioScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = portfolioViewModel,
                    salaryViewModel = viewModel
                )
            }
            composable("savings") {
                SavingsScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    portfolioViewModel = portfolioViewModel,
                    salaryViewModel = viewModel
                )
            }
            composable("allocation") {
                AllocationScreen(
                    profile = viewModel.profile.collectAsState().value,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
