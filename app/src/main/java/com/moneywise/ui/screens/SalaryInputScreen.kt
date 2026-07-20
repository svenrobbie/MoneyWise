package com.moneywise.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
    var atv by remember(profile.atvPercent) { mutableStateOf(profile.atvPercent.toString()) }
    var verlof by remember(profile.verlofPercent) { mutableStateOf(profile.verlofPercent.toString()) }
    var endOfYearBonus by remember(profile.endOfYearBonusPercent) { mutableStateOf(profile.endOfYearBonusPercent.toString()) }
    var overtimePercent by remember(profile.overtimePercent) { mutableStateOf(profile.overtimePercent.toString()) }
    var weekendPercent by remember(profile.weekendPercent) { mutableStateOf(profile.weekendPercent.toString()) }
    var nightPercent by remember(profile.nightPercent) { mutableStateOf(profile.nightPercent.toString()) }
    var shiftPercent by remember(profile.shiftPercent) { mutableStateOf(profile.shiftPercent.toString()) }

    var includeHolidayAllowance by remember(profile.includeHolidayAllowance) { mutableStateOf(profile.includeHolidayAllowance) }
    var includeAtv by remember(profile.includeAtv) { mutableStateOf(profile.includeAtv) }
    var includeVerlof by remember(profile.includeVerlof) { mutableStateOf(profile.includeVerlof) }
    var includeOvertime by remember(profile.includeOvertime) { mutableStateOf(profile.includeOvertime) }
    var includeWeekend by remember(profile.includeWeekend) { mutableStateOf(profile.includeWeekend) }
    var includeNight by remember(profile.includeNight) { mutableStateOf(profile.includeNight) }
    var includeShift by remember(profile.includeShift) { mutableStateOf(profile.includeShift) }

    var paymentFrequency by remember(profile.paymentFrequency) { mutableStateOf(profile.paymentFrequency) }

    var deductionName1 by remember(profile.deductionName1) { mutableStateOf(profile.deductionName1) }
    var deductionPercent1 by remember(profile.deductionPercent1) { mutableStateOf(profile.deductionPercent1.toString()) }
    var deductionEnabled1 by remember(profile.deductionEnabled1) { mutableStateOf(profile.deductionEnabled1) }
    var deductionName2 by remember(profile.deductionName2) { mutableStateOf(profile.deductionName2) }
    var deductionPercent2 by remember(profile.deductionPercent2) { mutableStateOf(profile.deductionPercent2.toString()) }
    var deductionEnabled2 by remember(profile.deductionEnabled2) { mutableStateOf(profile.deductionEnabled2) }
    var deductionName3 by remember(profile.deductionName3) { mutableStateOf(profile.deductionName3) }
    var deductionPercent3 by remember(profile.deductionPercent3) { mutableStateOf(profile.deductionPercent3.toString()) }
    var deductionEnabled3 by remember(profile.deductionEnabled3) { mutableStateOf(profile.deductionEnabled3) }

    var extraName1 by remember(profile.extraName1) { mutableStateOf(profile.extraName1) }
    var extraAmount1 by remember(profile.extraAmount1) { mutableStateOf(profile.extraAmount1.toString()) }
    var extraEnabled1 by remember(profile.extraEnabled1) { mutableStateOf(profile.extraEnabled1) }
    var extraName2 by remember(profile.extraName2) { mutableStateOf(profile.extraName2) }
    var extraAmount2 by remember(profile.extraAmount2) { mutableStateOf(profile.extraAmount2.toString()) }
    var extraEnabled2 by remember(profile.extraEnabled2) { mutableStateOf(profile.extraEnabled2) }
    var extraName3 by remember(profile.extraName3) { mutableStateOf(profile.extraName3) }
    var extraAmount3 by remember(profile.extraAmount3) { mutableStateOf(profile.extraAmount3.toString()) }
    var extraEnabled3 by remember(profile.extraEnabled3) { mutableStateOf(profile.extraEnabled3) }

    var overigeExpanded by remember { mutableStateOf(false) }
    var aftrekExpanded by remember { mutableStateOf(false) }
    var extrasExpanded by remember { mutableStateOf(false) }
    var taxConfigExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var selectedTaxConfig by remember(profile.taxConfigName) { mutableStateOf(profile.taxConfig) }
    var selectedCurrency by remember(profile.currencyCode) { mutableStateOf(profile.currency) }

    val previewProfile = remember(
        hourlyWage, hoursPerWeek, age, holidayAllowance, atv, verlof,
        endOfYearBonus, overtimePercent, weekendPercent, nightPercent, shiftPercent,
        includeHolidayAllowance, includeAtv, includeVerlof,
        includeOvertime, includeWeekend, includeNight, includeShift,
        paymentFrequency,
        deductionName1, deductionPercent1, deductionEnabled1,
        deductionName2, deductionPercent2, deductionEnabled2,
        deductionName3, deductionPercent3, deductionEnabled3,
        extraName1, extraAmount1, extraEnabled1,
        extraName2, extraAmount2, extraEnabled2,
        extraName3, extraAmount3, extraEnabled3,
        selectedTaxConfig, selectedCurrency
    ) {
        SalaryProfile(
            hourlyWage = hourlyWage.toDoubleOrNull() ?: 0.0,
            hoursPerWeek = hoursPerWeek.toDoubleOrNull() ?: 0.0,
            age = age.toIntOrNull() ?: 0,
            holidayAllowancePercent = holidayAllowance.toDoubleOrNull() ?: 0.0,
            atvPercent = atv.toDoubleOrNull() ?: 0.0,
            verlofPercent = verlof.toDoubleOrNull() ?: 0.0,
            endOfYearBonusPercent = endOfYearBonus.toDoubleOrNull() ?: 0.0,
            overtimePercent = overtimePercent.toDoubleOrNull() ?: 0.0,
            weekendPercent = weekendPercent.toDoubleOrNull() ?: 0.0,
            nightPercent = nightPercent.toDoubleOrNull() ?: 0.0,
            shiftPercent = shiftPercent.toDoubleOrNull() ?: 0.0,
            includeHolidayAllowance = includeHolidayAllowance,
            includeAtv = includeAtv,
            includeVerlof = includeVerlof,
            includeOvertime = includeOvertime,
            includeWeekend = includeWeekend,
            includeNight = includeNight,
            includeShift = includeShift,
            deductionName1 = deductionName1,
            deductionPercent1 = deductionPercent1.toDoubleOrNull() ?: 0.0,
            deductionEnabled1 = deductionEnabled1,
            deductionName2 = deductionName2,
            deductionPercent2 = deductionPercent2.toDoubleOrNull() ?: 0.0,
            deductionEnabled2 = deductionEnabled2,
            deductionName3 = deductionName3,
            deductionPercent3 = deductionPercent3.toDoubleOrNull() ?: 0.0,
            deductionEnabled3 = deductionEnabled3,
            extraName1 = extraName1,
            extraAmount1 = extraAmount1.toDoubleOrNull() ?: 0.0,
            extraEnabled1 = extraEnabled1,
            extraName2 = extraName2,
            extraAmount2 = extraAmount2.toDoubleOrNull() ?: 0.0,
            extraEnabled2 = extraEnabled2,
            extraName3 = extraName3,
            extraAmount3 = extraAmount3.toDoubleOrNull() ?: 0.0,
            extraEnabled3 = extraEnabled3,
            taxConfigName = selectedTaxConfig.name,
            currencyCode = selectedCurrency.code,
            paymentFrequency = paymentFrequency
        )
    }

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
            PreviewCard(profile = previewProfile, currency = selectedCurrency)

            HorizontalDivider()

            Text(
                text = "Basis gegevens",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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

            Text(
                text = "Betaalfrequentie",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = paymentFrequency == "monthly",
                    onClick = { paymentFrequency = "monthly" },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Maandelijks")
                }
                SegmentedButton(
                    selected = paymentFrequency == "4weekly",
                    onClick = { paymentFrequency = "4weekly" },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("4-wekelijks")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Hoofd toeslagen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            PercentFieldWithToggle(
                value = holidayAllowance,
                onValueChange = { holidayAllowance = it },
                label = "Vakantiegeld (%)",
                checked = includeHolidayAllowance,
                onCheckedChange = { includeHolidayAllowance = it }
            )

            PercentFieldWithToggle(
                value = atv,
                onValueChange = { atv = it },
                label = "ATV-toeslag (%)",
                checked = includeAtv,
                onCheckedChange = { includeAtv = it }
            )

            PercentFieldWithToggle(
                value = verlof,
                onValueChange = { verlof = it },
                label = "Verlof-toeslag (%)",
                checked = includeVerlof,
                onCheckedChange = { includeVerlof = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            OverigeToeslagenSection(
                expanded = overigeExpanded,
                onToggle = { overigeExpanded = !overigeExpanded },
                endOfYearBonus = endOfYearBonus,
                onEndOfYearBonusChange = { endOfYearBonus = it },
                overtimePercent = overtimePercent,
                onOvertimeChange = { overtimePercent = it },
                weekendPercent = weekendPercent,
                onWeekendChange = { weekendPercent = it },
                nightPercent = nightPercent,
                onNightChange = { nightPercent = it },
                shiftPercent = shiftPercent,
                onShiftChange = { shiftPercent = it },
                includeOvertime = includeOvertime,
                onIncludeOvertimeChange = { includeOvertime = it },
                includeWeekend = includeWeekend,
                onIncludeWeekendChange = { includeWeekend = it },
                includeNight = includeNight,
                onIncludeNightChange = { includeNight = it },
                includeShift = includeShift,
                onIncludeShiftChange = { includeShift = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            AftrekpostenSection(
                expanded = aftrekExpanded,
                onToggle = { aftrekExpanded = !aftrekExpanded },
                name1 = deductionName1, onName1Change = { deductionName1 = it },
                percent1 = deductionPercent1, onPercent1Change = { deductionPercent1 = it },
                enabled1 = deductionEnabled1, onEnabled1Change = { deductionEnabled1 = it },
                name2 = deductionName2, onName2Change = { deductionName2 = it },
                percent2 = deductionPercent2, onPercent2Change = { deductionPercent2 = it },
                enabled2 = deductionEnabled2, onEnabled2Change = { deductionEnabled2 = it },
                name3 = deductionName3, onName3Change = { deductionName3 = it },
                percent3 = deductionPercent3, onPercent3Change = { deductionPercent3 = it },
                enabled3 = deductionEnabled3, onEnabled3Change = { deductionEnabled3 = it }
            )

            OverigeExtrasSection(
                expanded = extrasExpanded,
                onToggle = { extrasExpanded = !extrasExpanded },
                name1 = extraName1, onName1Change = { extraName1 = it },
                amount1 = extraAmount1, onAmount1Change = { extraAmount1 = it },
                enabled1 = extraEnabled1, onEnabled1Change = { extraEnabled1 = it },
                name2 = extraName2, onName2Change = { extraName2 = it },
                amount2 = extraAmount2, onAmount2Change = { extraAmount2 = it },
                enabled2 = extraEnabled2, onEnabled2Change = { extraEnabled2 = it },
                name3 = extraName3, onName3Change = { extraName3 = it },
                amount3 = extraAmount3, onAmount3Change = { extraAmount3 = it },
                enabled3 = extraEnabled3, onEnabled3Change = { extraEnabled3 = it },
                currencySymbol = selectedCurrency.symbol
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Instellingen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
                            atvPercent = atv.toDoubleOrNull() ?: this.atvPercent,
                            verlofPercent = verlof.toDoubleOrNull() ?: this.verlofPercent,
                            endOfYearBonusPercent = endOfYearBonus.toDoubleOrNull() ?: this.endOfYearBonusPercent,
                            overtimePercent = overtimePercent.toDoubleOrNull() ?: this.overtimePercent,
                            weekendPercent = weekendPercent.toDoubleOrNull() ?: this.weekendPercent,
                            nightPercent = nightPercent.toDoubleOrNull() ?: this.nightPercent,
                            shiftPercent = shiftPercent.toDoubleOrNull() ?: this.shiftPercent,
                            includeHolidayAllowance = includeHolidayAllowance,
                            includeAtv = includeAtv,
                            includeVerlof = includeVerlof,
                            includeOvertime = includeOvertime,
                            includeWeekend = includeWeekend,
                            includeNight = includeNight,
                            includeShift = includeShift,
                            deductionName1 = deductionName1,
                            deductionPercent1 = deductionPercent1.toDoubleOrNull() ?: 0.0,
                            deductionEnabled1 = deductionEnabled1,
                            deductionName2 = deductionName2,
                            deductionPercent2 = deductionPercent2.toDoubleOrNull() ?: 0.0,
                            deductionEnabled2 = deductionEnabled2,
                            deductionName3 = deductionName3,
                            deductionPercent3 = deductionPercent3.toDoubleOrNull() ?: 0.0,
                            deductionEnabled3 = deductionEnabled3,
                            extraName1 = extraName1,
                            extraAmount1 = extraAmount1.toDoubleOrNull() ?: 0.0,
                            extraEnabled1 = extraEnabled1,
                            extraName2 = extraName2,
                            extraAmount2 = extraAmount2.toDoubleOrNull() ?: 0.0,
                            extraEnabled2 = extraEnabled2,
                            extraName3 = extraName3,
                            extraAmount3 = extraAmount3.toDoubleOrNull() ?: 0.0,
                            extraEnabled3 = extraEnabled3,
                            taxConfigName = selectedTaxConfig.name,
                            currencyCode = selectedCurrency.code,
                            paymentFrequency = paymentFrequency
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
private fun PercentFieldWithToggle(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            enabled = checked,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun OverigeToeslagenSection(
    expanded: Boolean,
    onToggle: () -> Unit,
    endOfYearBonus: String,
    onEndOfYearBonusChange: (String) -> Unit,
    overtimePercent: String,
    onOvertimeChange: (String) -> Unit,
    weekendPercent: String,
    onWeekendChange: (String) -> Unit,
    nightPercent: String,
    onNightChange: (String) -> Unit,
    shiftPercent: String,
    onShiftChange: (String) -> Unit,
    includeOvertime: Boolean,
    onIncludeOvertimeChange: (Boolean) -> Unit,
    includeWeekend: Boolean,
    onIncludeWeekendChange: (Boolean) -> Unit,
    includeNight: Boolean,
    onIncludeNightChange: (Boolean) -> Unit,
    includeShift: Boolean,
    onIncludeShiftChange: (Boolean) -> Unit
) {
    Column {
        CollapsibleHeader(
            title = "Overige toeslagen",
            expanded = expanded,
            onToggle = onToggle
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                PercentFieldWithToggle(
                    value = endOfYearBonus,
                    onValueChange = onEndOfYearBonusChange,
                    label = "Eindejaarsbonus (%)",
                    checked = true,
                    onCheckedChange = {}
                )

                PercentFieldWithToggle(
                    value = overtimePercent,
                    onValueChange = onOvertimeChange,
                    label = "Overwerktoeslag (%)",
                    checked = includeOvertime,
                    onCheckedChange = onIncludeOvertimeChange
                )

                PercentFieldWithToggle(
                    value = weekendPercent,
                    onValueChange = onWeekendChange,
                    label = "Weekendtoeslag (%)",
                    checked = includeWeekend,
                    onCheckedChange = onIncludeWeekendChange
                )

                PercentFieldWithToggle(
                    value = nightPercent,
                    onValueChange = onNightChange,
                    label = "Nachttoeslag (%)",
                    checked = includeNight,
                    onCheckedChange = onIncludeNightChange
                )

                PercentFieldWithToggle(
                    value = shiftPercent,
                    onValueChange = onShiftChange,
                    label = "Ploegentoeslag (%)",
                    checked = includeShift,
                    onCheckedChange = onIncludeShiftChange
                )
            }
        }
    }
}

@Composable
private fun AftrekpostenSection(
    expanded: Boolean,
    onToggle: () -> Unit,
    name1: String, onName1Change: (String) -> Unit,
    percent1: String, onPercent1Change: (String) -> Unit,
    enabled1: Boolean, onEnabled1Change: (Boolean) -> Unit,
    name2: String, onName2Change: (String) -> Unit,
    percent2: String, onPercent2Change: (String) -> Unit,
    enabled2: Boolean, onEnabled2Change: (Boolean) -> Unit,
    name3: String, onName3Change: (String) -> Unit,
    percent3: String, onPercent3Change: (String) -> Unit,
    enabled3: Boolean, onEnabled3Change: (Boolean) -> Unit
) {
    Column {
        CollapsibleHeader(
            title = "Aftrekposten (%)",
            expanded = expanded,
            onToggle = onToggle
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                DeductionRow(
                    name = name1, onNameChange = onName1Change,
                    percent = percent1, onPercentChange = onPercent1Change,
                    enabled = enabled1, onEnabledChange = onEnabled1Change,
                    defaultLabel = "Naam (bijv. Pensioen)"
                )
                DeductionRow(
                    name = name2, onNameChange = onName2Change,
                    percent = percent2, onPercentChange = onPercent2Change,
                    enabled = enabled2, onEnabledChange = onEnabled2Change,
                    defaultLabel = "Naam"
                )
                DeductionRow(
                    name = name3, onNameChange = onName3Change,
                    percent = percent3, onPercentChange = onPercent3Change,
                    enabled = enabled3, onEnabledChange = onEnabled3Change,
                    defaultLabel = "Naam"
                )
            }
        }
    }
}

@Composable
private fun DeductionRow(
    name: String, onNameChange: (String) -> Unit,
    percent: String, onPercentChange: (String) -> Unit,
    enabled: Boolean, onEnabledChange: (Boolean) -> Unit,
    defaultLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(defaultLabel) },
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = percent,
            onValueChange = onPercentChange,
            label = { Text("%") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.width(80.dp)
        )
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange
        )
    }
}

@Composable
private fun OverigeExtrasSection(
    expanded: Boolean,
    onToggle: () -> Unit,
    name1: String, onName1Change: (String) -> Unit,
    amount1: String, onAmount1Change: (String) -> Unit,
    enabled1: Boolean, onEnabled1Change: (Boolean) -> Unit,
    name2: String, onName2Change: (String) -> Unit,
    amount2: String, onAmount2Change: (String) -> Unit,
    enabled2: Boolean, onEnabled2Change: (Boolean) -> Unit,
    name3: String, onName3Change: (String) -> Unit,
    amount3: String, onAmount3Change: (String) -> Unit,
    enabled3: Boolean, onEnabled3Change: (Boolean) -> Unit,
    currencySymbol: String
) {
    Column {
        CollapsibleHeader(
            title = "Overige extra's (€)",
            expanded = expanded,
            onToggle = onToggle
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                ExtraRow(
                    name = name1, onNameChange = onName1Change,
                    amount = amount1, onAmountChange = onAmount1Change,
                    enabled = enabled1, onEnabledChange = onEnabled1Change,
                    defaultLabel = "Naam (bijv. Bonus)",
                    currencySymbol = currencySymbol
                )
                ExtraRow(
                    name = name2, onNameChange = onName2Change,
                    amount = amount2, onAmountChange = onAmount2Change,
                    enabled = enabled2, onEnabledChange = onEnabled2Change,
                    defaultLabel = "Naam",
                    currencySymbol = currencySymbol
                )
                ExtraRow(
                    name = name3, onNameChange = onName3Change,
                    amount = amount3, onAmountChange = onAmount3Change,
                    enabled = enabled3, onEnabledChange = onEnabled3Change,
                    defaultLabel = "Naam",
                    currencySymbol = currencySymbol
                )
            }
        }
    }
}

@Composable
private fun ExtraRow(
    name: String, onNameChange: (String) -> Unit,
    amount: String, onAmountChange: (String) -> Unit,
    enabled: Boolean, onEnabledChange: (Boolean) -> Unit,
    defaultLabel: String,
    currencySymbol: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(defaultLabel) },
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("${currencySymbol}bedrag") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.width(120.dp)
        )
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange
        )
    }
}

@Composable
private fun CollapsibleHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Inklappen" else "Uitklappen"
            )
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
            PreviewRow("Bruto ${profile.paymentPeriodLabel}", "${currency.symbol}${"%.2f".format(profile.grossPerPeriod)}")
            PreviewRow("Netto ${profile.paymentPeriodLabel}", "${currency.symbol}${"%.2f".format(profile.netPerPeriod)}")
            PreviewRow("Bruto per jaar", "${currency.symbol}${"%.2f".format(profile.totalAnnualGross)}")
            PreviewRow("Belastingpercentage", "${"%.1f".format(profile.effectiveTaxRate * 100)}%")
            if (profile.age > 0) {
                PreviewRow("Jaren tot pensioen", "${profile.retirementAge - profile.age}")
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
