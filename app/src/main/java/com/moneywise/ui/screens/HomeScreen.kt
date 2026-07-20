package com.moneywise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moneywise.data.Calculators
import com.moneywise.data.SalaryProfile
import com.moneywise.viewmodel.PortfolioViewModel
import com.moneywise.viewmodel.SalaryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun blurValue(value: String, blurred: Boolean): String {
    return if (blurred) "••••" else value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: SalaryViewModel,
    portfolioViewModel: PortfolioViewModel
) {
    val profile by viewModel.profile.collectAsState()
    val portfolio by portfolioViewModel.portfolio.collectAsState()
    val greeting = remember { getGreeting() }
    val currentDate = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("nl", "NL"))
        )
    }
    var isBlurred by remember { mutableStateOf(false) }

    val isInvestmentDue = remember(portfolio, profile) {
        portfolioViewModel.isInvestmentDue(profile.paymentFrequency)
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
                actions = {
                    IconButton(onClick = { isBlurred = !isBlurred }) {
                        Icon(
                            if (isBlurred) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isBlurred) "Tonen" else "Verbergen",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Instellingen",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
                .verticalScroll(rememberScrollState())
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

            if (isInvestmentDue) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💰", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Heb je al beleggd?",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        TextButton(
                            onClick = { portfolioViewModel.markInvestmentDone() }
                        ) {
                            Text("Ja, gedaan!")
                        }
                    }
                }
            }

            SalarySummaryCard(profile = profile, isBlurred = isBlurred)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("portfolio") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Portefeuille")
                }
                Button(
                    onClick = { navController.navigate("allocation") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verdeling")
                }
            }
        }
    }
}

@Composable
private fun SalarySummaryCard(profile: SalaryProfile, isBlurred: Boolean = false) {
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
                value = blurValue(Calculators.formatCurrency(profile.hourlyWage, profile.currency, true), isBlurred)
            )
            SummaryRow(
                label = "Netto ${profile.paymentPeriodLabel}",
                value = blurValue(Calculators.formatCurrency(profile.netPerPeriod, profile.currency, false), isBlurred)
            )
            SummaryRow(
                label = "Netto per jaar",
                value = blurValue(Calculators.formatCurrency(profile.netAnnual, profile.currency, false), isBlurred)
            )
            SummaryRow(
                label = "Bruto per jaar",
                value = blurValue(Calculators.formatCurrency(profile.totalAnnualGross, profile.currency, false), isBlurred)
            )
            if (profile.totalDeductionPercent > 0) {
                SummaryRow(
                    label = "Aftrekposten (${String.format("%.1f", profile.totalDeductionPercent)}%)",
                    value = blurValue(Calculators.formatCurrency(profile.annualDeductions, profile.currency, false), isBlurred)
                )
            }
            if (profile.annualExtras != 0.0) {
                SummaryRow(
                    label = "Overige extra's",
                    value = blurValue(Calculators.formatCurrency(profile.annualExtras, profile.currency, false), isBlurred)
                )
            }
            if (profile.age > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )
                SummaryRow(
                    label = "Leeftijd",
                    value = blurValue("${profile.age} jaar", isBlurred)
                )
                SummaryRow(
                    label = "Jaren tot pensioen",
                    value = blurValue("${profile.yearsUntilRetirement} jaar (op ${profile.retirementAge})", isBlurred)
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
