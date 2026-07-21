package com.moneywise.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneywise.data.SalaryProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SalaryViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    private val _profile = MutableStateFlow(SalaryProfile())
    val profile: StateFlow<SalaryProfile> = _profile.asStateFlow()

    private val _darkModeOverride = MutableStateFlow<Boolean?>(null)
    val darkModeOverride: StateFlow<Boolean?> = _darkModeOverride.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val key = stringPreferencesKey("salary_profile")
            prefs[key]?.let { stored ->
                try {
                    _profile.value = json.decodeFromString(stored)
                } catch (_: Exception) {}
            }
            val darkKey = stringPreferencesKey("dark_mode")
            prefs[darkKey]?.let { stored ->
                _darkModeOverride.value = when (stored) {
                    "true" -> true
                    "false" -> false
                    else -> null
                }
            }
        }
    }

    fun setDarkMode(mode: Boolean?) {
        _darkModeOverride.value = mode
        viewModelScope.launch {
            try {
                val key = stringPreferencesKey("dark_mode")
                dataStore.edit { prefs ->
                    prefs[key] = when (mode) {
                        true -> "true"
                        false -> "false"
                        null -> "system"
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun updateProfile(transform: SalaryProfile.() -> SalaryProfile) {
        _profile.update { it.transform() }
        save()
    }

    private fun save() {
        viewModelScope.launch {
            try {
                val key = stringPreferencesKey("salary_profile")
                dataStore.edit { prefs ->
                    prefs[key] = json.encodeToString(_profile.value)
                }
            } catch (_: Exception) {}
        }
    }
}
