package com.moneywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    MoneyWiseNavHost()
                }
            }
        }
    }
}

@Composable
fun MoneyWiseNavHost() {
    val navController = rememberNavController()
    val viewModel: SalaryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("salary") {
            SalaryInputScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("worktime") {
            WorkTimeScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("savings") {
            SavingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("investment") {
            InvestmentScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
