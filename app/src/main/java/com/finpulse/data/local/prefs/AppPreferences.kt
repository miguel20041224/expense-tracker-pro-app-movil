package com.finpulse.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.finpulse.util.LocaleHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("finpulse_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val seededKey = booleanPreferencesKey("demo_seeded")
    private val userIdKey = longPreferencesKey("logged_in_user_id")
    private val localeKey = stringPreferencesKey("app_locale")

    val isDemoSeeded: Flow<Boolean> = context.dataStore.data.map { it[seededKey] ?: false }

    val loggedInUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[userIdKey]?.takeIf { it > 0L }
    }

    val appLocale: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[localeKey] ?: LocaleHelper.LOCALE_ES
    }

    suspend fun markDemoSeeded() {
        context.dataStore.edit { it[seededKey] = true }
    }

    suspend fun setLoggedInUserId(userId: Long?) {
        context.dataStore.edit { prefs ->
            if (userId == null) prefs.remove(userIdKey) else prefs[userIdKey] = userId
        }
    }

    suspend fun setAppLocale(localeTag: String) {
        context.dataStore.edit { it[localeKey] = localeTag }
    }

    suspend fun currentLocale(): String = appLocale.first()
}
