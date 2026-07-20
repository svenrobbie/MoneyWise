package com.moneywise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            SalarySummaryCard(profile = profile)

            OutlinedButton(
                onClick = { navController.navigate("salary") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salaris invoeren")
            }
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
                value = Calculators.formatCurrency(profile.hourlyWage, profile.currency, true)
            )
            SummaryRow(
                label = "Netto ${profile.paymentPeriodLabel}",
                value = Calculators.formatCurrency(profile.netPerPeriod, profile.currency, false)
            )
            SummaryRow(
                label = "Netto per jaar",
                value = Calculators.formatCurrency(profile.netAnnual, profile.currency, false)
            )
            SummaryRow(
                label = "Bruto per jaar",
                value = Calculators.formatCurrency(profile.totalAnnualGross, profile.currency, false)
            )
            if (profile.totalDeductionPercent > 0) {
                SummaryRow(
                    label = "Aftrekposten (${String.format("%.1f", profile.totalDeductionPercent)}%)",
                    value = Calculators.formatCurrency(profile.annualDeductions, profile.currency, false)
                )
            }
            if (profile.annualExtras != 0.0) {
                SummaryRow(
                    label = "Overige extra's",
                    value = Calculators.formatCurrency(profile.annualExtras, profile.currency, false)
                )
            }
            if (profile.age > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )
                SummaryRow(
                    label = "Leeftijd",
                    value = "${profile.age} jaar"
                )
                SummaryRow(
                    label = "Jaren tot pensioen",
                    value = "${profile.yearsUntilRetirement} jaar (op ${profile.retirementAge})"
                )
            }
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

private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "Goedemorgen"
        hour < 18 -> "Goedemiddag"
        else -> "Goedenavond"
    }
}
