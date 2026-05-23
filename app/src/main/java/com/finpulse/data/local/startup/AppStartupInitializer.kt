package com.finpulse.data.local.startup

import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.data.local.seed.DemoDataSeeder
import com.finpulse.data.session.CurrentUserProvider
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
    private val currentUser: CurrentUserProvider,
) {
    suspend fun run(userId: Long) {
        try {
            currentUser.setUserId(userId)
            seeder.ensureCatalog(userId)
            if (!preferences.isDemoSeeded.first()) {
                seeder.seedIfEmpty(userId)
                preferences.markDemoSeeded()
            }
            refreshIntelligence()
        } finally {
            bootstrapState.markReady()
        }
    }
}
