package com.moneywise.worker

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moneywise.data.Portfolio
import com.moneywise.data.SalaryProfile
import com.moneywise.viewmodel.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class InvestmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val json = Json { ignoreUnknownKeys = true }
    private val appDataStore get() = (applicationContext as Application).dataStore

    override suspend fun doWork(): Result {
        val profile = loadProfile() ?: return Result.success()
        val portfolio = loadPortfolio() ?: return Result.success()

        if (portfolio.monthlyAmount <= 0) return Result.success()

        val lastCheck = portfolio.lastInvestmentCheck
        if (lastCheck.isNotEmpty()) {
            val daysSince = try {
                val lastDate = java.time.LocalDate.parse(lastCheck)
                java.time.temporal.ChronoUnit.DAYS.between(lastDate, java.time.LocalDate.now())
            } catch (_: Exception) { 999 }

            val threshold = if (profile.is4Weekly) 25L else 27L
            if (daysSince < threshold) return Result.success()
        }

        NotificationHelper.showReminder(applicationContext)
        return Result.success()
    }

    private suspend fun loadProfile(): SalaryProfile? {
        return try {
            val prefs = appDataStore.data.first()
            prefs[stringPreferencesKey("salary_profile")]?.let {
                json.decodeFromString(it)
            }
        } catch (_: Exception) { null }
    }

    private suspend fun loadPortfolio(): Portfolio? {
        return try {
            val prefs = appDataStore.data.first()
            prefs[stringPreferencesKey("portfolio")]?.let {
                json.decodeFromString(it)
            }
        } catch (_: Exception) { null }
    }
}
