package com.moneywise.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moneywise.data.Currency
import com.moneywise.data.SalaryProfile
import com.moneywise.data.TaxConfig
import com.moneywise.viewmodel.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryInputScreen(
    viewModel: SalaryViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()

    var hourlyWage by remember(profile.hourlyWage) { mutableStateOf(profile.hourlyWage.toString()) }
    var hoursPerWeek by remember(profile.hoursPerWeek) { mutableStateOf(profile.hoursPerWeek.toString()) }
    var age by remember(profile.age) { mutableStateOf(profile.age.toString()) }
    var holidayAllowance by remember(profile.holidayAllowancePercent) { mutableStateOf(profile.holidayAllowancePercent.toString()) }
    var endOfYearBonus by remember(profile.endOfYearBonusPercent) { mutableStateOf(profile.endOfYearBonusPercent.toString()) }
    var overtimePercent by remember(profile.overtimePercent) { mutableStateOf(profile.overtimePercent.toString()) }
    var weekendPercent by remember(profile.weekendPercent) { mutableStateOf(profile.weekendPercent.toString()) }
    var nightPercent by remember(profile.nightPercent) { mutableStateOf(profile.nightPercent.toString()) }
    var shiftPercent by remember(profile.shiftPercent) { mutableStateOf(profile.shiftPercent.toString()) }

    var taxConfigExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var selectedTaxConfig by remember(profile.taxConfigName) { mutableStateOf(profile.taxConfig) }
    var selectedCurrency by remember(profile.currencyCode) { mutableStateOf(profile.currency) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salaris gegevens") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PreviewCard(profile = profile, currency = selectedCurrency)

            HorizontalDivider()

            OutlinedTextField(
                value = hourlyWage,
                onValueChange = { hourlyWage = it },
                label = { Text("Uurloon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = hoursPerWeek,
                onValueChange = { hoursPerWeek = it },
                label = { Text("Uren per week") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Leeftijd") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = holidayAllowance,
                onValueChange = { holidayAllowance = it },
                label = { Text("Vakantiegeld (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endOfYearBonus,
                onValueChange = { endOfYearBonus = it },
                label = { Text("Eindejaarsbonus (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = overtimePercent,
                onValueChange = { overtimePercent = it },
                label = { Text("Overwerktoeslag (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weekendPercent,
                onValueChange = { weekendPercent = it },
                label = { Text("Weekendtoeslag (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nightPercent,
                onValueChange = { nightPercent = it },
                label = { Text("Nachttoeslag (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = shiftPercent,
                onValueChange = { shiftPercent = it },
                label = { Text("Ploegentoeslag (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = taxConfigExpanded,
                onExpandedChange = { taxConfigExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedTaxConfig.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Belastingregio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = taxConfigExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = taxConfigExpanded,
                    onDismissRequest = { taxConfigExpanded = false }
                ) {
                    TaxConfig.all.forEach { config ->
                        DropdownMenuItem(
                            text = { Text(config.name) },
                            onClick = {
                                selectedTaxConfig = config
                                taxConfigExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = it }
            ) {
                OutlinedTextField(
                    value = "${selectedCurrency.code} (${selectedCurrency.symbol})",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Valuta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    Currency.all.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text("${currency.code} (${currency.symbol}) - ${currency.name}") },
                            onClick = {
                                selectedCurrency = currency
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.updateProfile {
                        copy(
                            hourlyWage = hourlyWage.toDoubleOrNull() ?: this.hourlyWage,
                            hoursPerWeek = hoursPerWeek.toDoubleOrNull() ?: this.hoursPerWeek,
                            age = age.toIntOrNull() ?: this.age,
                            holidayAllowancePercent = holidayAllowance.toDoubleOrNull() ?: this.holidayAllowancePercent,
                            endOfYearBonusPercent = endOfYearBonus.toDoubleOrNull() ?: this.endOfYearBonusPercent,
                            overtimePercent = overtimePercent.toDoubleOrNull() ?: this.overtimePercent,
                            weekendPercent = weekendPercent.toDoubleOrNull() ?: this.weekendPercent,
                            nightPercent = nightPercent.toDoubleOrNull() ?: this.nightPercent,
                            shiftPercent = shiftPercent.toDoubleOrNull() ?: this.shiftPercent,
                            taxConfigName = selectedTaxConfig.name,
                            currencyCode = selectedCurrency.code
                        )
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Opslaan")
            }
        }
    }
}

@Composable
private fun PreviewCard(
    profile: SalaryProfile,
    currency: Currency
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Overzicht",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            PreviewRow("Bruto per maand", "${currency.symbol}${"%.2f".format(profile.monthlyGross)}")
            PreviewRow("Netto per maand", "${currency.symbol}${"%.2f".format(profile.netMonthly)}")
            PreviewRow("Bruto per jaar", "${currency.symbol}${"%.2f".format(profile.totalAnnualGross)}")
            PreviewRow("Belastingpercentage", "${"%.1f".format(profile.effectiveTaxRate * 100)}%")
            if (profile.age > 0) {
                PreviewRow("Jaren tot pensioen", "${67 - profile.age}")
            }
        }
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
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
            fontWeight = FontWeight.SemiBold
        )
    }
}
