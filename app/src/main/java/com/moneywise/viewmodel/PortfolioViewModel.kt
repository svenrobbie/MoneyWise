package com.moneywise.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneywise.data.Portfolio
import com.moneywise.data.PortfolioHolding
import com.moneywise.data.RebalanceAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PortfolioViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val json = Json { ignoreUnknownKeys = true }
    private val jsonPretty = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val _portfolio = MutableStateFlow(Portfolio())
    val portfolio: StateFlow<Portfolio> = _portfolio.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val key = stringPreferencesKey("portfolio")
            prefs[key]?.let { stored ->
                try {
                    _portfolio.value = json.decodeFromString(stored)
                } catch (_: Exception) {}
            }
        }
    }

    fun addHolding(holding: PortfolioHolding) {
        _portfolio.update { it.copy(holdings = it.holdings + holding) }
        save()
    }

    fun removeHolding(symbol: String) {
        _portfolio.update { p ->
            p.copy(holdings = p.holdings.filter { it.symbol != symbol })
        }
        save()
    }

    fun updateHolding(symbol: String, transform: PortfolioHolding.() -> PortfolioHolding) {
        _portfolio.update { p ->
            p.copy(holdings = p.holdings.map { if (it.symbol == symbol) it.transform() else it })
        }
        save()
    }

    fun updateCurrentPrice(symbol: String, price: Double) {
        updateHolding(symbol) { copy(currentPrice = price) }
    }

    fun buyMore(symbol: String, additionalShares: Double, pricePerShare: Double) {
        updateHolding(symbol) {
            val newTotalShares = shares + additionalShares
            val newAvgPrice = ((shares * avgPurchasePrice) + (additionalShares * pricePerShare)) / newTotalShares
            copy(
                shares = newTotalShares,
                avgPurchasePrice = newAvgPrice,
                currentPrice = pricePerShare
            )
        }
    }

    fun updateWallet(amount: Double) {
        _portfolio.update { it.copy(wallet = amount) }
        save()
    }

    fun updateMonthlyAmount(amount: Double) {
        _portfolio.update { it.copy(monthlyAmount = amount) }
        save()
    }

    fun markInvestmentDone() {
        _portfolio.update { it.copy(lastInvestmentCheck = LocalDate.now().toString()) }
        save()
    }

    fun isInvestmentDue(paymentFrequency: String): Boolean {
        if (_portfolio.value.monthlyAmount <= 0) return false
        val lastCheck = _portfolio.value.lastInvestmentCheck
        if (lastCheck.isEmpty()) return true
        val lastDate = try { LocalDate.parse(lastCheck) } catch (_: Exception) { return true }
        val daysSince = java.time.temporal.ChronoUnit.DAYS.between(lastDate, LocalDate.now())
        return when (paymentFrequency) {
            "4weekly" -> daysSince >= 25
            else -> daysSince >= 27
        }
    }

    fun getTotalInvested(): Double {
        return _portfolio.value.holdings.sumOf { it.shares * it.avgPurchasePrice }
    }

    fun getTotalValue(): Double {
        return _portfolio.value.holdings.sumOf { it.shares * it.currentPrice }
    }

    fun calculateRebalance(monthlyAmount: Double): List<RebalanceAction> {
        val portfolio = _portfolio.value
        if (portfolio.holdings.isEmpty() || monthlyAmount <= 0) return emptyList()

        val totalValue = portfolio.holdings.sumOf { it.shares * it.currentPrice }
        if (totalValue <= 0) return emptyList()

        return portfolio.holdings.map { h ->
            val currentValue = h.shares * h.currentPrice
            val currentPercent = (currentValue / totalValue) * 100.0
            val targetValue = totalValue * (h.targetPercent / 100.0)
            val diff = targetValue - currentValue

            if (diff > 0) {
                val totalDeficit = portfolio.holdings.sumOf { d ->
                    val cv = d.shares * d.currentPrice
                    val tv = totalValue * (d.targetPercent / 100.0)
                    maxOf(0.0, tv - cv)
                }
                val spendable = if (totalDeficit > 0) monthlyAmount * (diff / totalDeficit) else 0.0
                val sharesToBuy = if (h.currentPrice > 0) (spendable / h.currentPrice).toInt() else 0
                val amountToSpend = sharesToBuy * h.currentPrice
                val leftOver = spendable - amountToSpend
                val insufficientFunds = sharesToBuy == 0 && spendable > 0

                RebalanceAction(
                    symbol = h.symbol, name = h.name,
                    currentValue = currentValue, currentPercent = currentPercent,
                    targetValue = targetValue, targetPercent = h.targetPercent,
                    diff = diff, sharesToBuy = sharesToBuy,
                    amountToSpend = amountToSpend, leftOver = leftOver,
                    insufficientFunds = insufficientFunds
                )
            } else {
                RebalanceAction(
                    symbol = h.symbol, name = h.name,
                    currentValue = currentValue, currentPercent = currentPercent,
                    targetValue = targetValue, targetPercent = h.targetPercent,
                    diff = diff, sharesToBuy = 0, amountToSpend = 0.0, leftOver = 0.0
                )
            }
        }
    }

    fun sellHolding(symbol: String, sharesToSell: Double, pricePerShare: Double) {
        val holding = _portfolio.value.holdings.find { it.symbol == symbol } ?: return
        val sellAmount = sharesToSell * pricePerShare
        val remainingShares = holding.shares - sharesToSell
        _portfolio.update { p ->
            val updatedHoldings = if (remainingShares <= 0.001) {
                p.holdings.filter { it.symbol != symbol }
            } else {
                p.holdings.map { if (it.symbol == symbol) it.copy(shares = remainingShares) else it }
            }
            p.copy(holdings = updatedHoldings, wallet = p.wallet + sellAmount)
        }
        save()
    }

    fun getExportJson(): String {
        val exportData = PortfolioExport(
            portfolio = _portfolio.value,
            exportedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        return jsonPretty.encodeToString(exportData)
    }

    fun getExportCsv(): String {
        val sb = StringBuilder()
        sb.appendLine("Symbool;Naam;Aantal;Aankoopprijs;Huidigeprijs;Doel%;Waarde;Winst/verlies")
        for (h in _portfolio.value.holdings) {
            val value = h.shares * h.currentPrice
            val gain = value - (h.shares * h.avgPurchasePrice)
            sb.appendLine("${h.symbol};${h.name};${h.shares};${h.avgPurchasePrice};${h.currentPrice};${h.targetPercent};${"%.2f".format(value)};${"%.2f".format(gain)}")
        }
        sb.appendLine()
        sb.appendLine("Portemonnee;;${_portfolio.value.wallet}")
        sb.appendLine("Maandbedrag;;${_portfolio.value.monthlyAmount}")
        return sb.toString()
    }

    fun importPortfolio(jsonString: String): Boolean {
        return try {
            val importData = json.decodeFromString<PortfolioExport>(jsonString)
            _portfolio.value = importData.portfolio.copy(lastUpdated = System.currentTimeMillis())
            save()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun save() {
        viewModelScope.launch {
            try {
                val key = stringPreferencesKey("portfolio")
                dataStore.edit { prefs ->
                    prefs[key] = json.encodeToString(_portfolio.value.copy(lastUpdated = System.currentTimeMillis()))
                }
            } catch (_: Exception) {}
        }
    }
}

@Serializable
data class PortfolioExport(
    val version: Int = 1,
    val exportedAt: String,
    val portfolio: Portfolio
)
