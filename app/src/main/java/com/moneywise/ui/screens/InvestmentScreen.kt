package com.moneywise.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moneywise.data.Calculators
import com.moneywise.data.Currency
import com.moneywise.viewmodel.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(
    viewModel: SalaryViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val currency = remember(profile) { profile.currency }

    var startAmount by remember { mutableStateOf("") }
    var monthlyAmount by remember { mutableStateOf("") }
    var returnPercent by remember { mutableStateOf("7") }
    var inflationPercent by remember { mutableStateOf("2.5") }
    val initialYears = if (profile.age > 0) profile.yearsUntilRetirement.toFloat() else 30f
    var years by remember { mutableFloatStateOf(initialYears.coerceIn(1f, 50f)) }

    val parsedStart = startAmount.toDoubleOrNull() ?: 0.0
    val parsedMonthly = monthlyAmount.toDoubleOrNull() ?: 0.0
    val parsedReturn = returnPercent.toDoubleOrNull() ?: 0.0
    val parsedInflation = inflationPercent.toDoubleOrNull() ?: 0.0
    val parsedYears = years.toInt().coerceIn(1, 50)

    val hasInput = parsedStart > 0.0 || parsedMonthly > 0.0

    val result = remember(parsedStart, parsedMonthly, parsedReturn, parsedYears, parsedInflation) {
        Calculators.calculateInvestment(parsedStart, parsedMonthly, parsedReturn, parsedYears, parsedInflation)
    }

    val savingsResult = remember(parsedStart, parsedMonthly, parsedYears) {
        Calculators.calculateInvestment(parsedStart, parsedMonthly, 1.3, parsedYears, 0.0)
    }

    val multiplier = if (result.totalContributed > 0) result.finalAmount / result.totalContributed else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beleggen") },
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
                value = startAmount,
                onValueChange = { startAmount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Startbedrag") },
                prefix = { Text(currency.symbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = monthlyAmount,
                onValueChange = { monthlyAmount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Maandelijkse inleg") },
                prefix = { Text(currency.symbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = returnPercent,
                onValueChange = { returnPercent = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Verwacht rendement") },
                suffix = { Text("%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = inflationPercent,
                onValueChange = { inflationPercent = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Inflatie") },
                suffix = { Text("%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Looptijd", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "$parsedYears jaar",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = years,
                    onValueChange = { years = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!hasInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Voer bedragen in om te zien wat je beleggingen waard kunnen worden",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (hasInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nominaal vs Reëel",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Nominaal: het daadwerkelijke bedrag op je rekening, zonder inflatie-correctie.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Reëel: de koopkracht van dat bedrag, gecorrigeerd voor inflatie. Wat je er daadwerkelijk voor kunt kopen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            if (hasInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Als je dit $parsedYears jaar volhoudt, heb je op je ${profile.age + parsedYears}-jarige leeftijd ${Calculators.formatCurrency(result.finalAmount, currency)} op je rekening staan",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        ResultRow(
                            label = "Nominaal saldo",
                            value = Calculators.formatCurrency(result.finalAmount, currency)
                        )
                        ResultRow(
                            label = "Reëel saldo",
                            value = Calculators.formatCurrency(result.finalRealAmount, currency)
                        )
                        ResultRow(
                            label = "Totaal ingelegd",
                            value = Calculators.formatCurrency(result.totalContributed, currency)
                        )
                        ResultRow(
                            label = "Nominaal rendement",
                            value = Calculators.formatCurrency(result.totalGain, currency)
                        )
                        ResultRow(
                            label = "Reëel rendement",
                            value = Calculators.formatCurrency(result.totalRealGain, currency)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "Dit is ${String.format("%.1fx", multiplier)} je inleg",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Vergeleken met sparen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bij sparen met 1,3% rente zou je na $parsedYears jaar ${Calculators.formatCurrency(savingsResult.finalAmount, currency)} hebben",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val difference = result.finalAmount - savingsResult.finalAmount
                        ResultRow(
                            label = "Beleggen",
                            value = Calculators.formatCurrency(result.finalAmount, currency)
                        )
                        ResultRow(
                            label = "Sparen (1,3%)",
                            value = Calculators.formatCurrency(savingsResult.finalAmount, currency)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        ResultRow(
                            label = "Verschil",
                            value = Calculators.formatCurrency(difference, currency)
                        )
                    }
                }

                if (result.timeline.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ontwikkeling",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            InvestmentChart(
                                timeline = result.timeline,
                                currency = currency,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ChartLegend(color = Color(0xFF009688), label = "Nominaal")
                                ChartLegend(color = Color(0xFFFF9800), label = "Reëel")
                                ChartLegend(color = Color(0xFF9E9E9E), label = "Ingelegd")
                            }
                        }
                    }
                }

                if (result.timeline.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Jaaroverzicht",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            val milestones = remember(result) {
                                buildMilestones(result.timeline, currency)
                            }
                            milestones.forEach { milestone ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = milestone.year,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = milestone.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = milestone.value,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1.2f),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ChartLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawLine(color = color, start = Offset(0f, size.height / 2), end = Offset(size.width, size.height / 2), strokeWidth = 4f)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun InvestmentChart(
    timeline: List<com.moneywise.data.InvestmentYear>,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    val nominalColor = Color(0xFF009688)
    val realColor = Color(0xFFFF9800)
    val contributedColor = Color(0xFF9E9E9E)

    val nominalValues = remember(timeline) { timeline.map { it.nominalBalance } }
    val realValues = remember(timeline) { timeline.map { it.realBalance } }
    val contributedValues = remember(timeline) { timeline.map { it.contributed } }

    val maxValue = remember(nominalValues, realValues, contributedValues) {
        listOf(
            nominalValues.maxOrNull() ?: 0.0,
            realValues.maxOrNull() ?: 0.0,
            contributedValues.maxOrNull() ?: 0.0
        ).maxOrNull()?.let { if (it == 0.0) 1.0 else it } ?: 1.0
    }

    Canvas(modifier = modifier) {
        val leftPadding = 0f
        val bottomPadding = 0f
        val chartWidth = size.width - leftPadding
        val chartHeight = size.height - bottomPadding

        if (timeline.size < 2) return@Canvas

        val xStep = chartWidth / (timeline.size - 1)

        fun drawSeries(values: List<Double>, color: Color) {
            val path = Path()
            values.forEachIndexed { index, value ->
                val x = leftPadding + index * xStep
                val y = chartHeight - (value / maxValue * chartHeight).toFloat()
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color, style = Stroke(width = 3f))
        }

        drawSeries(contributedValues, contributedColor)
        drawSeries(realValues, realColor)
        drawSeries(nominalValues, nominalColor)
    }
}

private data class Milestone(val year: String, val label: String, val value: String)

private fun buildMilestones(
    timeline: List<com.moneywise.data.InvestmentYear>,
    currency: Currency
): List<Milestone> {
    val milestones = mutableListOf<Milestone>()
    val totalContributed = timeline.lastOrNull()?.contributed ?: 0.0

    milestones.add(
        Milestone(
            year = "Begin",
            label = "Ingelegd",
            value = Calculators.formatCurrency(timeline.firstOrNull()?.contributed ?: 0.0, currency)
        )
    )

    val halfWayIndex = timeline.indexOfFirst { it.nominalBalance >= totalContributed * 2 }
    if (halfWayIndex >= 0) {
        milestones.add(
            Milestone(
                year = "Jaar ${timeline[halfWayIndex].year}",
                label = "2x inleg",
                value = Calculators.formatCurrency(timeline[halfWayIndex].nominalBalance, currency)
            )
        )
    }

    val quarterPoints = listOf(10, 20, 30, 40, 50)
    quarterPoints.forEach { targetYear ->
        val entry = timeline.find { it.year == targetYear }
        if (entry != null) {
            milestones.add(
                Milestone(
                    year = "Jaar ${entry.year}",
                    label = "Nominaal",
                    value = Calculators.formatCurrency(entry.nominalBalance, currency)
                )
            )
        }
    }

    val lastEntry = timeline.lastOrNull()
    if (lastEntry != null) {
        milestones.add(
            Milestone(
                year = "Jaar ${lastEntry.year}",
                label = "Totaal",
                value = Calculators.formatCurrency(lastEntry.nominalBalance, currency)
            )
        )
    }

    return milestones.distinctBy { "${it.year}${it.label}" }
}
