package com.moneywise.data

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioHolding(
    val symbol: String,
    val name: String,
    val shares: Double,
    val avgPurchasePrice: Double,
    val currentPrice: Double = avgPurchasePrice,
    val targetPercent: Double
)

@Serializable
data class Portfolio(
    val holdings: List<PortfolioHolding> = emptyList(),
    val wallet: Double = 0.0,
    val monthlyAmount: Double = 0.0,
    val lastInvestmentCheck: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class RebalanceAction(
    val symbol: String,
    val name: String,
    val currentValue: Double,
    val currentPercent: Double,
    val targetValue: Double,
    val targetPercent: Double,
    val diff: Double,
    val sharesToBuy: Int,
    val amountToSpend: Double,
    val leftOver: Double,
    val insufficientFunds: Boolean = false
)
