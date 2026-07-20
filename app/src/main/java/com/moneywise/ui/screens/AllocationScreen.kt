package com.moneywise.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moneywise.data.SalaryProfile

enum class ProfileType(val label: String, val description: String) {
    STUDENT("Student", "Ik studeer nog"),
    ADULT("Volwassene", "Ik werk fulltime")
}

enum class Level(val label: String, val description: String) {
    BEGINNER("Beginner", "Nog niet eerder gespaard of belegd"),
    INTERMEDIATE("Gemiddeld", "Spaar al, maar beleg nog niet"),
    ADVANCED("Gevorderd", "Beleg al actief")
}

data class AllocationResult(
    val savePercent: Double,
    val investPercent: Double,
    val saveAmount: Double,
    val investAmount: Double,
    val explanation: String,
    val tips: List<String>
)

fun calculateAllocation(
    profile: ProfileType,
    level: Level,
    monthlyAmount: Double
): AllocationResult {
    val (savePercent, investPercent) = when (profile) {
        ProfileType.STUDENT -> when (level) {
            Level.BEGINNER -> 80.0 to 20.0
            Level.INTERMEDIATE -> 60.0 to 40.0
            Level.ADVANCED -> 40.0 to 60.0
        }
        ProfileType.ADULT -> when (level) {
            Level.BEGINNER -> 70.0 to 30.0
            Level.INTERMEDIATE -> 50.0 to 50.0
            Level.ADVANCED -> 30.0 to 70.0
        }
    }

    val saveAmount = monthlyAmount * savePercent / 100.0
    val investAmount = monthlyAmount * investPercent / 100.0

    val explanation = when (profile) {
        ProfileType.STUDENT -> when (level) {
            Level.BEGINNER ->
                "Als student-beginner is een spaarbuffer cruciaal. " +
                "80% sparen zorgt voor een veilige basis, terwijl 20% beleggen je laat wennen aan beleggen zonder groot risico."
            Level.INTERMEDIATE ->
                "Je hebt al ervaring met sparen. Nu kun je meer beleggen voor langere-termijn groei. " +
                "60/40 is een gezonde balans tussen veiligheid en rendement."
            Level.ADVANCED ->
                "Met je beleggingservaring kun je meer risico nemen. " +
                "60% beleggen maximaliseert je rendement, terwijl 40% sparen nog steeds een veilige buffer biedt."
        }
        ProfileType.ADULT -> when (level) {
            Level.BEGINNER ->
                "Als volwassene begin je met een solide spaarbuffer. " +
                "70% sparen bouwt eerst veiligheid op, 30% beleggen laat je groeien."
            Level.INTERMEDIATE ->
                "Een evenwichtige 50/50 verdeling werkt goed voor jou. " +
                "Sparen voor korte termijn, beleggen voor je pensioen en andere langetermijndoelen."
            Level.ADVANCED ->
                "Met een sterke spaarbasis kun je meer beleggen. " +
                "70% beleggen bouwt vermogen op, 30% sparen biedt nog steeds een veilige buffer."
        }
    }

    val tips = when (profile) {
        ProfileType.STUDENT -> listOf(
            "Zet eerst een buffer van 1-2 maanden kosten opzij",
            "Beleg alleen wat je 5+ jaar kunt missen",
            "Gebruik DeGiro kernselectie voor lage transactiekosten",
            "Begin met een wereldwijde ETF zoals VWRL of IWDA"
        )
        ProfileType.ADULT -> listOf(
            "Bouw een noodfonds van 3-6 maanden vaste lasten",
            "Gebruik je jaarruimte voor pensioenbeleggen",
            "Verhoog je inleg naarmate je salaris stijgt",
            "Spreid over verschillende ETF's en sectoren"
        )
    }

    return AllocationResult(
        savePercent = savePercent,
        investPercent = investPercent,
        saveAmount = saveAmount,
        investAmount = investAmount,
        explanation = explanation,
        tips = tips
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationScreen(
    profile: SalaryProfile,
    onBack: () -> Unit
) {
    var selectedProfile by remember { mutableStateOf<ProfileType?>(null) }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var inputMode by remember { mutableStateOf("amount") }
    var amountInput by remember { mutableStateOf("") }
    var percentInput by remember { mutableStateOf("") }

    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val parsedPercent = percentInput.toDoubleOrNull() ?: 0.0
    val monthlyAmount = when {
        inputMode == "amount" && parsedAmount > 0 -> parsedAmount
        inputMode == "percent" && parsedPercent > 0 -> profile.netMonthly * parsedPercent / 100.0
        else -> 0.0
    }

    val result = remember(selectedProfile, selectedLevel, monthlyAmount) {
        if (selectedProfile != null && selectedLevel != null && monthlyAmount > 0) {
            calculateAllocation(selectedProfile!!, selectedLevel!!, monthlyAmount)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verdeling") },
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
            Text(
                text = "Sparen & Beleggen Verdeling",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bepaal hoeveel je wilt sparen en beleggen op basis van je profiel.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            HorizontalDivider()

            Text(
                text = "Wat is je situatie?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedProfile == type,
                        onClick = { selectedProfile = type },
                        label = {
                            Column {
                                Text(type.label, fontWeight = FontWeight.Medium)
                                Text(type.description, style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (selectedProfile != null) {
                HorizontalDivider()

                Text(
                    text = "Wat is je ervaring?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Level.entries.forEach { level ->
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { selectedLevel = level },
                            label = {
                                Column {
                                    Text(level.label, fontWeight = FontWeight.Medium)
                                    Text(level.description, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (selectedProfile != null && selectedLevel != null) {
                HorizontalDivider()

                Text(
                    text = "Hoeveel kun je per maand besteden?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = inputMode == "amount",
                        onClick = { inputMode = "amount" },
                        label = { Text("Bedrag (€)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = inputMode == "percent",
                        onClick = { inputMode = "percent" },
                        label = { Text("% van netto loon") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (inputMode == "amount") {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Bedrag per maand") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = percentInput,
                        onValueChange = { percentInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Percentage van netto loon") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (parsedPercent > 0) {
                        Text(
                            text = "€${String.format("%.2f", profile.netMonthly * parsedPercent / 100.0)} per maand",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (result != null) {
                HorizontalDivider()

                Text(
                    text = "Jouw verdeling",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AllocationBar(
                            savePercent = result.savePercent,
                            investPercent = result.investPercent,
                            saveAmount = result.saveAmount,
                            investAmount = result.investAmount
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sparen",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "€${String.format("%.0f", result.saveAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Beleggen",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "€${String.format("%.0f", result.investAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

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
                            text = "Waarom deze verdeling?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = result.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tips",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        result.tips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            if (selectedProfile != null && selectedLevel != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Disclaimer: Dit advies is algemeen en geen financieel advies. Raadpleeg een financieel adviseur voor persoonlijk advies.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllocationBar(
    savePercent: Double,
    investPercent: Double,
    saveAmount: Double,
    investAmount: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(savePercent.toFloat())
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.tertiary)
                )
                Box(
                    modifier = Modifier
                        .weight(investPercent.toFloat())
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sparen ${String.format("%.0f", savePercent)}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Beleggen ${String.format("%.0f", investPercent)}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
