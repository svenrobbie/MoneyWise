package com.moneywise.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moneywise.data.Calculators
import com.moneywise.data.PortfolioHolding
import com.moneywise.viewmodel.PortfolioViewModel
import com.moneywise.viewmodel.SalaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    salaryViewModel: SalaryViewModel,
    onBack: () -> Unit
) {
    val portfolio by viewModel.portfolio.collectAsState()
    val profile by salaryViewModel.profile.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditMonthly by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    val totalInvested = remember(portfolio) { viewModel.getTotalInvested() }
    val totalValue = remember(portfolio) { viewModel.getTotalValue() }
    val totalGain = totalValue - totalInvested
    val totalGainPercent = if (totalInvested > 0) (totalGain / totalInvested) * 100.0 else 0.0

    val parsedMonthly = portfolio.monthlyAmount
    val availableAmount = if (portfolio.wallet > 0) minOf(parsedMonthly, portfolio.wallet) else parsedMonthly
    val rebalanceActions = remember(availableAmount, portfolio) {
        if (availableAmount > 0) viewModel.calculateRebalance(availableAmount) else emptyList()
    }
    val totalLeftOver = rebalanceActions.sumOf { it.leftOver }
    val rebalanceBySymbol = remember(rebalanceActions) { rebalanceActions.associateBy { it.symbol } }

    val yearsUntilRetirement = if (profile.yearsUntilRetirement > 0) profile.yearsUntilRetirement else 30

    val goalScenarios = remember(totalValue, parsedMonthly, yearsUntilRetirement) {
        if (parsedMonthly > 0 && yearsUntilRetirement > 0) {
            Calculators.calculatePortfolioProjection(
                currentBalance = totalValue,
                monthlyContribution = parsedMonthly,
                years = yearsUntilRetirement
            )
        } else emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mijn Portefeuille") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                PortfolioSummaryCard(totalInvested, totalValue, totalGain, totalGainPercent, portfolio.wallet)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Maandbedrag", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text(
                            text = if (parsedMonthly > 0) "€${String.format("%.0f", parsedMonthly)}" else "Niet ingesteld",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { showEditMonthly = true }) {
                        Text("Bewerken")
                    }
                }
            }

            if (portfolio.holdings.isNotEmpty()) {
                item {
                    Text(
                        text = "Aandelen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(portfolio.holdings) { holding ->
                    HoldingCard(
                        holding = holding,
                        totalValue = totalValue,
                        rebalanceAction = rebalanceBySymbol[holding.symbol],
                        onRemove = { viewModel.removeHolding(holding.symbol) },
                        onUpdateTarget = { newPercent ->
                            viewModel.updateHolding(holding.symbol) { copy(targetPercent = newPercent) }
                        },
                        onUpdateShares = { newShares ->
                            viewModel.updateHolding(holding.symbol) { copy(shares = newShares) }
                        },
                        onUpdatePrice = { newPrice ->
                            viewModel.updateCurrentPrice(holding.symbol, newPrice)
                        },
                        onBuyMore = { shares, price ->
                            viewModel.buyMore(holding.symbol, shares, price)
                        },
                        onSell = { shares, price ->
                            viewModel.sellHolding(holding.symbol, shares, price)
                        }
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aandeel toevoegen")
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Portemonnee", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text(
                            text = "€${String.format("%.2f", portfolio.wallet)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDepositDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stort")
                        }
                        if (portfolio.wallet > 0) {
                            OutlinedButton(onClick = { showWithdrawDialog = true }) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Opnemen")
                            }
                        }
                    }
                }
            }

            if (parsedMonthly > 0 && rebalanceActions.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Column {
                        Text(
                            text = "Advies: wat te kopen",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (portfolio.wallet in 0.01..parsedMonthly) {
                            Text(
                                text = "Beschikbaar: €${String.format("%.0f", portfolio.wallet)} (van €${String.format("%.0f", parsedMonthly)} maandbedrag)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                items(rebalanceActions) { action ->
                    RebalanceCard(action = action)
                }

                if (rebalanceActions.any { it.insufficientFunds }) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Niet genoeg saldo om te kopen",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                if (totalLeftOver > 0.01) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Wisselgeld",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "€${String.format("%.2f", totalLeftOver)} over na aankopen",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Dit bedrag blijft op je exchange staan voor de volgende keer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            if (parsedMonthly > 0 && totalValue > 0 && goalScenarios.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text(
                        text = "Projectie (${yearsUntilRetirement} jaar)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(goalScenarios) { scenario ->
                    GoalScenarioCard(scenario = scenario, currencySymbol = "€", years = yearsUntilRetirement)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }

    if (showAddDialog) {
        AddHoldingDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { holding ->
                viewModel.addHolding(holding)
                showAddDialog = false
            }
        )
    }

    if (showEditMonthly) {
        var monthlyInput by remember { mutableStateOf(
            if (parsedMonthly > 0) parsedMonthly.toInt().toString() else ""
        ) }
        AlertDialog(
            onDismissRequest = { showEditMonthly = false },
            title = { Text("Maandbedrag instellen") },
            text = {
                Column {
                    Text(
                        text = "Hoeveel wil je per maand beleggen?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = monthlyInput,
                        onValueChange = { monthlyInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Maandbedrag") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = monthlyInput.toDoubleOrNull() ?: 0.0
                        viewModel.updateMonthlyAmount(amount)
                        showEditMonthly = false
                    }
                ) { Text("Opslaan") }
            },
            dismissButton = { TextButton(onClick = { showEditMonthly = false }) { Text("Annuleren") } }
        )
    }

    if (showDepositDialog) {
        var depositInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("Geld storten") },
            text = {
                Column {
                    Text(
                        text = "Hoeveel heb je gestort op je exchange?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = depositInput,
                        onValueChange = { depositInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Bedrag gestort") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = depositInput.toDoubleOrNull() ?: return@TextButton
                        if (amount > 0) {
                            viewModel.updateWallet(portfolio.wallet + amount)
                            showDepositDialog = false
                        }
                    },
                    enabled = (depositInput.toDoubleOrNull() ?: 0.0) > 0
                ) { Text("Storten") }
            },
            dismissButton = { TextButton(onClick = { showDepositDialog = false }) { Text("Annuleren") } }
        )
    }

    if (showWithdrawDialog) {
        var withdrawInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Geld opnemen") },
            text = {
                Column {
                    Text(
                        text = "Hoeveel wil je opnemen van je exchange?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Beschikbaar: €${String.format("%.2f", portfolio.wallet)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = withdrawInput,
                        onValueChange = { withdrawInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Bedrag opnemen") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = withdrawInput.toDoubleOrNull() ?: return@TextButton
                        if (amount > 0 && amount <= portfolio.wallet) {
                            viewModel.updateWallet(portfolio.wallet - amount)
                            showWithdrawDialog = false
                        }
                    },
                    enabled = (withdrawInput.toDoubleOrNull() ?: 0.0) > 0 && (withdrawInput.toDoubleOrNull() ?: 0.0) <= portfolio.wallet
                ) { Text("Opnemen") }
            },
            dismissButton = { TextButton(onClick = { showWithdrawDialog = false }) { Text("Annuleren") } }
        )
    }
}

@Composable
private fun PortfolioSummaryCard(totalInvested: Double, totalValue: Double, totalGain: Double, totalGainPercent: Double, wallet: Double = 0.0) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Geïnvesteerd", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Text("€${String.format("%,.2f", totalInvested)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Huidige waarde", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Text("€${String.format("%,.2f", totalValue)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            if (wallet > 0.01) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Portemonnee", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text("€${String.format("%,.2f", wallet)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Winst/verlies", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Text(
                    text = "${if (totalGain >= 0) "+" else ""}€${String.format("%,.2f", totalGain)} (${String.format("%.1f", totalGainPercent)}%)",
                    fontWeight = FontWeight.Bold,
                    color = if (totalGain >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun HoldingCard(
    holding: PortfolioHolding,
    totalValue: Double,
    rebalanceAction: com.moneywise.data.RebalanceAction? = null,
    onRemove: () -> Unit,
    onUpdateTarget: (Double) -> Unit,
    onUpdateShares: (Double) -> Unit,
    onUpdatePrice: (Double) -> Unit,
    onBuyMore: (Double, Double) -> Unit,
    onSell: (Double, Double) -> Unit
) {
    val invested = holding.shares * holding.avgPurchasePrice
    val currentValue = holding.shares * holding.currentPrice
    val gain = currentValue - invested
    val gainPercent = if (invested > 0) (gain / invested) * 100.0 else 0.0
    val currentPercent = if (totalValue > 0) (currentValue / totalValue) * 100.0 else 0.0
    var showEdit by remember { mutableStateOf(false) }
    var showBuyMore by remember { mutableStateOf(false) }
    var showSell by remember { mutableStateOf(false) }
    var priceInput by remember(holding.currentPrice) { mutableStateOf(holding.currentPrice.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .clickable { showEdit = !showEdit }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${holding.symbol}  ${holding.name}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("${if (holding.shares % 1.0 == 0.0) holding.shares.toInt().toString() else String.format("%.2f", holding.shares)} stuks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("€${String.format("%,.2f", currentValue)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "${if (gain >= 0) "+" else ""}€${String.format("%.0f", gain)} (${String.format("%.1f", gainPercent)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (gain >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Geïnvesteerd: €${String.format("%,.0f", invested)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Text("Huidig: €${String.format("%,.0f", currentValue)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Doel: ${String.format("%.1f", holding.targetPercent)}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text("Huidig: ${String.format("%.1f", currentPercent)}%", style = MaterialTheme.typography.bodySmall)
                }
            }

            LinearProgressIndicator(
                progress = { (currentPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                color = if (kotlin.math.abs(currentPercent - holding.targetPercent) < 2.0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            AnimatedVisibility(visible = showEdit) {
                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showBuyMore = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Koop bij")
                    }
                    OutlinedButton(
                        onClick = { showSell = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verkopen")
                    }
                    OutlinedTextField(
                        value = if (holding.shares % 1.0 == 0.0) holding.shares.toInt().toString() else holding.shares.toString(),
                        onValueChange = { it.toDoubleOrNull()?.let { s -> onUpdateShares(s) } },
                        label = { Text("Aantal aandelen") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Huidige koers") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { priceInput.toDoubleOrNull()?.let { onUpdatePrice(it) } }) {
                                Text("OK")
                            }
                        }
                    )
                    OutlinedTextField(
                        value = if (holding.targetPercent % 1.0 == 0.0) holding.targetPercent.toInt().toString() else String.format("%.1f", holding.targetPercent),
                        onValueChange = { it.toDoubleOrNull()?.let { p -> onUpdateTarget(p) } },
                        label = { Text("Doel %") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = onRemove,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verwijderen")
                    }
                }
            }
        }
    }

    if (showBuyMore) {
        var buyShares by remember { mutableStateOf("") }
        var buyPrice by remember { mutableStateOf(holding.currentPrice.toString()) }
        AlertDialog(
            onDismissRequest = { showBuyMore = false },
            title = { Text("Bij kopen: ${holding.symbol}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (rebalanceAction != null && rebalanceAction.sharesToBuy > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Advies op basis van doelverdeling", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                val deficit = rebalanceAction.targetPercent - rebalanceAction.currentPercent
                                Text("Tekort: ${String.format("%.1f", deficit)}% onder doel", style = MaterialTheme.typography.bodySmall)
                                Text("Suggestie: koop ${rebalanceAction.sharesToBuy} stuk${if (rebalanceAction.sharesToBuy > 1) "s" else ""} voor €${String.format("%.2f", rebalanceAction.amountToSpend)}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else if (rebalanceAction != null && rebalanceAction.currentPercent >= rebalanceAction.targetPercent) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Dit aandeel zit al op of boven doel", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = buyShares,
                        onValueChange = { buyShares = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Aantal nieuwe eenheden") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = buyPrice,
                        onValueChange = { buyPrice = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Prijs per stuk") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val newShares = buyShares.toDoubleOrNull() ?: 0.0
                    val newPrice = buyPrice.toDoubleOrNull() ?: 0.0
                    if (newShares > 0 && newPrice > 0) {
                        val totalNew = newShares * newPrice
                        val combinedShares = holding.shares + newShares
                        val newAvg = ((holding.shares * holding.avgPurchasePrice) + totalNew) / combinedShares
                        Text(
                            text = "Nieuw gemiddelde: €${String.format("%.2f", newAvg)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Totaal: ${combinedShares.toInt()} stuks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val s = buyShares.toDoubleOrNull() ?: return@TextButton
                        val p = buyPrice.toDoubleOrNull() ?: return@TextButton
                        if (s > 0 && p > 0) {
                            onBuyMore(s, p)
                            showBuyMore = false
                        }
                    },
                    enabled = (buyShares.toDoubleOrNull() ?: 0.0) > 0 && (buyPrice.toDoubleOrNull() ?: 0.0) > 0
                ) { Text("Kopen") }
            },
            dismissButton = { TextButton(onClick = { showBuyMore = false }) { Text("Annuleren") } }
        )
    }

    if (showSell) {
        var sellShares by remember { mutableStateOf(if (holding.shares % 1.0 == 0.0) holding.shares.toInt().toString() else String.format("%.2f", holding.shares)) }
        var sellPrice by remember { mutableStateOf(holding.currentPrice.toString()) }
        val parsedSellShares = sellShares.toDoubleOrNull() ?: 0.0
        val parsedSellPrice = sellPrice.toDoubleOrNull() ?: 0.0
        val sellTotal = parsedSellShares * parsedSellPrice
        AlertDialog(
            onDismissRequest = { showSell = false },
            title = { Text("Verkopen: ${holding.symbol}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Beschikbaar: ${if (holding.shares % 1.0 == 0.0) holding.shares.toInt().toString() else String.format("%.2f", holding.shares)} stuks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    OutlinedTextField(
                        value = sellShares,
                        onValueChange = { sellShares = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Aantal te verkopen") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sellPrice,
                        onValueChange = { sellPrice = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Verkoopprijs per stuk") },
                        prefix = { Text("€") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (parsedSellShares > 0 && parsedSellPrice > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Verkoopsom", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = "€${String.format("%.2f", sellTotal)} wordt aan je portemonnee toegevoegd",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                val remaining = holding.shares - parsedSellShares
                                Text(
                                    text = if (remaining > 0.001) "Resterend: ${remaining.toInt()} stuks" else "Volledig verkocht",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val s = sellShares.toDoubleOrNull() ?: return@TextButton
                        val p = sellPrice.toDoubleOrNull() ?: return@TextButton
                        if (s > 0 && p > 0 && s <= holding.shares) {
                            onSell(s, p)
                            showSell = false
                        }
                    },
                    enabled = parsedSellShares > 0 && parsedSellPrice > 0 && parsedSellShares <= holding.shares
                ) { Text("Verkopen") }
            },
            dismissButton = { TextButton(onClick = { showSell = false }) { Text("Annuleren") } }
        )
    }
}

@Composable
private fun GoalScenarioCard(scenario: com.moneywise.data.GoalScenario, currencySymbol: String, years: Int = 30) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (scenario.label) {
                "Fantastisch" -> MaterialTheme.colorScheme.tertiaryContainer
                "Redelijk" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = scenario.label,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${String.format("%.0f", scenario.annualReturn)}% rendement",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            if (scenario.futureValue > 0) {
                Text(
                    text = "Na $years jaar: $currencySymbol${String.format("%,.0f", scenario.futureValue)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (scenario.monthlyContribution > 0) {
                Text(
                    text = "Maandelijks: $currencySymbol${String.format("%.0f", scenario.monthlyContribution)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Totaal ingelegd: $currencySymbol${String.format("%,.0f", scenario.totalContributed)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Rendement: $currencySymbol${String.format("%,.0f", scenario.totalGain)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (scenario.totalGain >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun RebalanceCard(action: com.moneywise.data.RebalanceAction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                action.sharesToBuy > 0 -> MaterialTheme.colorScheme.secondaryContainer
                action.insufficientFunds -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(action.symbol, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("${String.format("%.1f", action.currentPercent)}% → ${String.format("%.1f", action.targetPercent)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            if (action.sharesToBuy > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Koop ${action.sharesToBuy} stuk${if (action.sharesToBuy > 1) "s" else ""}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("€${String.format("%.2f", action.amountToSpend)}", style = MaterialTheme.typography.bodySmall)
                }
            } else if (action.insufficientFunds) {
                Text("Niet genoeg saldo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
            } else {
                Text("Geen actie", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun AddHoldingDialog(
    onDismiss: () -> Unit,
    onAdd: (PortfolioHolding) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var shares by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var targetPercent by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aandeel toevoegen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it.uppercase() },
                    label = { Text("Symbool") },
                    placeholder = { Text("bijv. VWCE, ASML") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Naam") },
                    placeholder = { Text("bijv. Vanguard All-World") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shares,
                    onValueChange = { shares = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Aantal eenheden") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Aankoopprijs per stuk") },
                    prefix = { Text("€") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetPercent,
                    onValueChange = { targetPercent = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Doelverdeling") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val shareCount = shares.toDoubleOrNull() ?: return@TextButton
                    val p = price.toDoubleOrNull() ?: return@TextButton
                    val t = targetPercent.toDoubleOrNull() ?: return@TextButton
                    if (symbol.isBlank()) return@TextButton
                    onAdd(PortfolioHolding(
                        symbol = symbol.trim(),
                        name = name.trim().ifBlank { symbol.trim() },
                        shares = shareCount,
                        avgPurchasePrice = p,
                        currentPrice = p,
                        targetPercent = t
                    ))
                },
                enabled = symbol.isNotBlank() && shares.toDoubleOrNull() != null && price.toDoubleOrNull() != null && targetPercent.toDoubleOrNull() != null
            ) { Text("Toevoegen") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuleren") } }
    )
}
