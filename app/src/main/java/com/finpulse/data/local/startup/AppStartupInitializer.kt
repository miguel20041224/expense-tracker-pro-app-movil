package com.finpulse.data.local.startup

import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.data.local.seed.DemoDataSeeder
import com.finpulse.domain.usecase.RefreshFinancialIntelligenceUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStartupInitializer @Inject constructor(
    private val preferences: AppPreferences,
    private val seeder: DemoDataSeeder,
    private val refreshIntelligence: RefreshFinancialIntelligenceUseCase,
    private val bootstrapState: AppBootstrapState,
) {
    suspend fun run() {
        try {
            if (!preferences.isDemoSeeded.first()) {
                seeder.seedIfEmpty()
                preferences.markDemoSeeded()
            }
            refreshIntelligence()
        } finally {
            bootstrapState.markReady()
        }
    }
}
