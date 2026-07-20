package com.moneywise.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.moneywise.data.Calculators
import com.moneywise.viewmodel.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkTimeScreen(
    viewModel: SalaryViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    var priceText by remember { mutableStateOf("") }

    val price = priceText.toDoubleOrNull() ?: 0.0
    val result = remember(price, profile) {
        if (price > 0.0) Calculators.calculateWorkTime(price, profile) else null
    }
    val comparisons = remember(price) { Calculators.getPurchaseComparisons(price) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Werktijd Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Prijs") },
                prefix = { Text("${profile.currency.symbol} ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5.0, 10.0, 25.0, 50.0, 100.0).forEach { amount ->
                    FilterChip(
                        selected = priceText == amount.toLong().toString(),
                        onClick = { priceText = amount.toLong().toString() },
                        label = {
                            Text(Calculators.formatCurrency(amount, profile.currency, showDecimals = false))
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(250.0, 500.0, 1000.0, 2500.0, 5000.0).forEach { amount ->
                    FilterChip(
                        selected = priceText == amount.toLong().toString(),
                        onClick = { priceText = amount.toLong().toString() },
                        label = {
                            Text(Calculators.formatCurrency(amount, profile.currency, showDecimals = false))
                        }
                    )
                }
            }

            if (price <= 0.0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Voer een bedrag in om te zien hoe lang je ervoor moet werken",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (result != null && price > 0.0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Om ${Calculators.formatCurrency(price, profile.currency)} te kunnen betalen...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider()

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Netto werktijd",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = result.humanReadable,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Bruto werktijd",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = result.humanReadableGross,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        HorizontalDivider()

                        val ratio = price / profile.hourlyWage
                        Text(
                            text = "Dit is ${"%.1f".format(ratio)}x je uurloon (${Calculators.formatCurrency(profile.hourlyWage, profile.currency, showDecimals = false)}/uur)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        val totalWeeklyHours = profile.hoursPerWeek
                        val progress = (result.hours / totalWeeklyHours).toFloat().coerceIn(0f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Aandeel in je werkweek",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                            )
                            Text(
                                text = "${"%.1f".format(progress * 100)}% van je ${"%.0f".format(totalWeeklyHours)}-urige werkweek",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        }

                        if (comparisons.isNotEmpty()) {
                            HorizontalDivider()

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Dat is vergelijkbaar met:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                comparisons.forEach { comparison ->
                                    Text(
                                        text = "${comparison.emoji} ${comparison.count} ${comparison.label}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (profile.yearsUntilRetirement > 0) {
                            HorizontalDivider()

                            val investmentResult = remember(price, profile) {
                                Calculators.calculateInvestment(
                                    initialAmount = price,
                                    monthlyContribution = 0.0,
                                    annualReturn = 7.0,
                                    years = profile.yearsUntilRetirement
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Als je dit geld zou sparen op je leeftijd (${profile.age} jaar), heb je op je ${profile.retirementAge}e...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = Calculators.formatCurrency(investmentResult.finalAmount, profile.currency),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = " (${Calculators.formatCurrency(investmentResult.totalGain, profile.currency)} winst na ${profile.yearsUntilRetirement} jaar bij 7% rendement)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
