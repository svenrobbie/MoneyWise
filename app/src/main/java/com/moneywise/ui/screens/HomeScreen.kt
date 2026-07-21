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
import com.moneywise.viewmodel.PortfolioViewModel
import com.moneywise.viewmodel.SalaryViewModel
import com.moneywise.ui.components.ResultRow
import com.moneywise.ui.components.SummaryCard
import com.moneywise.ui.components.blurValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            SummaryCard(title = "Salaris Overzicht") {
                ResultRow(
                    label = "Uurloon",
                    value = blurValue(Calculators.formatCurrency(profile.hourlyWage, profile.currency, true), isBlurred),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                ResultRow(
                    label = "Netto ${profile.paymentPeriodLabel}",
                    value = blurValue(Calculators.formatCurrency(profile.netPerPeriod, profile.currency, false), isBlurred),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                ResultRow(
                    label = "Netto per jaar",
                    value = blurValue(Calculators.formatCurrency(profile.netAnnual, profile.currency, false), isBlurred),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                ResultRow(
                    label = "Bruto per jaar",
                    value = blurValue(Calculators.formatCurrency(profile.totalAnnualGross, profile.currency, false), isBlurred),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (profile.totalDeductionPercent > 0) {
                    ResultRow(
                        label = "Aftrekposten (${String.format("%.1f", profile.totalDeductionPercent)}%)",
                        value = blurValue(Calculators.formatCurrency(profile.annualDeductions, profile.currency, false), isBlurred),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (profile.annualExtras != 0.0) {
                    ResultRow(
                        label = "Overige extra's",
                        value = blurValue(Calculators.formatCurrency(profile.annualExtras, profile.currency, false), isBlurred),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (profile.age > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    ResultRow(
                        label = "Leeftijd",
                        value = blurValue("${profile.age} jaar", isBlurred),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ResultRow(
                        label = "Jaren tot pensioen",
                        value = blurValue("${profile.yearsUntilRetirement} jaar (op ${profile.retirementAge})", isBlurred),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        valueColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    onClick = { navController.navigate("portfolio") },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Portefeuille",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    onClick = { navController.navigate("allocation") },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Verdeling",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    onClick = { navController.navigate("savings") },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Savings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sparen",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
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
                            Text("\uD83D\uDCB0", style = MaterialTheme.typography.titleMedium)
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
