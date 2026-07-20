package com.moneywise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PiggyBank
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moneywise.data.Calculators
import com.moneywise.data.SalaryProfile
import com.moneywise.viewmodel.SalaryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: SalaryViewModel
) {
    val profile by viewModel.profile.collectAsState()
    val greeting = remember { getGreeting() }
    val currentDate = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("nl", "NL"))
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "MoneyWise",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item(span = { GridCells.Fixed(2) }) {
                GreetingCard(greeting = greeting, date = currentDate)
            }
            item(span = { GridCells.Fixed(2) }) {
                SalarySummaryCard(profile = profile)
            }
            item(span = { GridCells.Fixed(1) }) {
                NavCard(
                    icon = Icons.Default.AccountBalance,
                    label = "Salaris",
                    subtitle = "Bereken je salaris",
                    onClick = { navController.navigate("salary") }
                )
            }
            item(span = { GridCells.Fixed(1) }) {
                NavCard(
                    icon = Icons.Default.Work,
                    label = "Werktijd",
                    subtitle = "Bereken werktijd",
                    onClick = { navController.navigate("worktime") }
                )
            }
            item(span = { GridCells.Fixed(1) }) {
                NavCard(
                    icon = Icons.Default.PiggyBank,
                    label = "Sparen",
                    subtitle = "Sparen simulator",
                    onClick = { navController.navigate("savings") }
                )
            }
            item(span = { GridCells.Fixed(1) }) {
                NavCard(
                    icon = Icons.Default.TrendingUp,
                    label = "Beleggen",
                    subtitle = "Beleggen simulator",
                    onClick = { navController.navigate("investment") }
                )
            }
        }
    }
}

@Composable
private fun GreetingCard(greeting: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SalarySummaryCard(profile: SalaryProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Salaris Overzicht",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(
                label = "Uurloon",
                value = Calculators.formatCurrency(profile.hourlyWage, profile.currency, false)
            )
            SummaryRow(
                label = "Netto per maand",
                value = Calculators.formatCurrency(profile.netMonthly, profile.currency, false)
            )
            SummaryRow(
                label = "Netto per jaar",
                value = Calculators.formatCurrency(profile.netAnnual, profile.currency, false)
            )
            SummaryRow(
                label = "Bruto per jaar",
                value = Calculators.formatCurrency(profile.totalAnnualGross, profile.currency, false)
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun NavCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "Goedemorgen"
        hour < 18 -> "Goedemiddag"
        else -> "Goedenavond"
    }
}