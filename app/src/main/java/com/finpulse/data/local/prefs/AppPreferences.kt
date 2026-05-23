package com.finpulse.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("finpulse_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val seededKey = booleanPreferencesKey("demo_seeded")

    val isDemoSeeded: Flow<Boolean> = context.dataStore.data.map { it[seededKey] ?: false }

    suspend fun markDemoSeeded() {
        context.dataStore.edit { it[seededKey] = true }
    }
}
