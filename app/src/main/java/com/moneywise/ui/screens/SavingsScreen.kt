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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moneywise.data.Calculators
import com.moneywise.data.SavingsResult
import com.moneywise.viewmodel.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsScreen(
    onBack: () -> Unit,
    viewModel: SalaryViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()

    var initialText by remember { mutableStateOf("0") }
    var monthlyText by remember { mutableStateOf("0") }
    var rateText by remember { mutableStateOf("2.5") }
    var years by remember { mutableIntStateOf(30) }
    var goalText by remember { mutableStateOf("") }

    val initial = initialText.toDoubleOrNull() ?: 0.0
    val monthly = monthlyText.toDoubleOrNull() ?: 0.0
    val rate = rateText.toDoubleOrNull() ?: 0.0
    val goalAmount = goalText.toDoubleOrNull() ?: 0.0

    val hasInput = initial > 0.0 || monthly > 0.0

    val result = remember(initial, monthly, rate, years) {
        Calculators.calculateSavings(initial, monthly, rate, years)
    }

    val monthsToGoal = remember(monthly, rate, goalAmount, initial) {
        if (goalAmount > 0) Calculators.calculateMonthsToGoal(monthly, rate, goalAmount, initial) else null
    }

    val currency = profile.currency
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val outline = MaterialTheme.colorScheme.outlineVariant

    val yearsUntilRetirement = profile.yearsUntilRetirement

    val retirementResult = remember(initial, monthly, rate, yearsUntilRetirement) {
        if (yearsUntilRetirement > 0) Calculators.calculateSavings(initial, monthly, rate, yearsUntilRetirement) else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sparen") },
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Invoer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600)
                    OutlinedTextField(
                        value = initialText,
                        onValueChange = { initialText = it },
                        label = { Text("Bedrag") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = monthlyText,
                        onValueChange = { monthlyText = it },
                        label = { Text("Maandelijkse inleg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text("Jaarlijks rentepercentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Jaren: $years")
                    Slider(
                        value = years.toFloat(),
                        onValueChange = { years = it.toInt() },
                        valueRange = 1f..50f,
                        steps = 48
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Spaardoel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600)
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { goalText = it },
                        label = { Text("Doelbedrag") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (monthsToGoal != null) {
                        val maanden = monthsToGoal
                        if (maanden < 12) {
                            Text(
                                "Je hebt $maanden maanden nodig om dit te bereiken",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W500
                            )
                        } else {
                            val jaren = maanden / 12
                            val restMaanden = maanden % 12
                            val text = if (restMaanden == 0) {
                                "Dit duurt $jaren jaar"
                            } else {
                                "Dit duurt $jaren jaar en $restMaanden maanden"
                            }
                            Text(
                                text,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W500
                            )
                        }
                    } else if (goalAmount > 0 && monthly <= 0) {
                        Text(
                            "Voer een maandelijkse inleg in om het aantal maanden te berekenen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (!hasInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        "Voer bedragen in om te zien hoe je spaargeld groeit",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Eindsaldo", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text(
                        Calculators.formatCurrency(result.finalAmount, currency),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ingelegd", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                        Text(Calculators.formatCurrency(result.totalContributed, currency),
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Card(modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Rente", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
                        Text(Calculators.formatCurrency(result.totalInterest, currency),
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }

            if (retirementResult != null && hasInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Bij pensioen (${profile.retirementAge} jaar)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            Calculators.formatCurrency(retirementResult.finalAmount, currency),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Na $yearsUntilRetirement jaar (${Calculators.formatCurrency(retirementResult.totalContributed, currency, showDecimals = false)} ingelegd)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (result.timeline.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Grafiek", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600)
                        Spacer(modifier = Modifier.height(12.dp))
                        SavingsChart(result = result, primary = primary, secondary = secondary, outline = outline)
                    }
                }
            }

            if (result.timeline.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tijdlijn", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W600)
                        Spacer(modifier = Modifier.height(8.dp))
                        val milestones = result.timeline.filter {
                            it.year == 1 || it.year == result.years || it.year % 5 == 0
                        }
                        for (year in milestones) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Jaar ${year.year}", fontWeight = FontWeight.W600,
                                    style = MaterialTheme.typography.bodySmall)
                                Text(Calculators.formatCurrency(year.totalBalance, currency, showDecimals = false),
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            if (year != milestones.last()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsChart(result: SavingsResult, primary: Color, secondary: Color, outline: Color) {
    val balanceSpots = result.timeline.map { it.year.toFloat() to it.totalBalance.toFloat() }
    val contributedSpots = result.timeline.map { it.year.toFloat() to it.contributed.toFloat() }
    val maxY = (result.finalAmount * 1.05).toFloat().coerceAtLeast(1000f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val w = size.width
        val h = size.height
        val padding = 8f
        val chartW = w - padding * 2
        val chartH = h - padding * 2

        fun toX(year: Float) = padding + (year / result.years) * chartW
        fun toY(value: Float) = padding + chartH - (value / maxY) * chartH

        val balancePath = Path()
        balanceSpots.forEachIndexed { i, (x, y) ->
            val px = toX(x)
            val py = toY(y)
            if (i == 0) balancePath.moveTo(px, py) else balancePath.lineTo(px, py)
        }
        drawPath(balancePath, primary, style = Stroke(width = 3f))

        val contribPath = Path()
        contributedSpots.forEachIndexed { i, (x, y) ->
            val px = toX(x)
            val py = toY(y)
            if (i == 0) contribPath.moveTo(px, py) else contribPath.lineTo(px, py)
        }
        drawPath(contribPath, secondary, style = Stroke(width = 2f))
    }
}
