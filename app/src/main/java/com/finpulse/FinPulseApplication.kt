package com.finpulse

import android.app.Application
import com.finpulse.data.local.startup.AppStartupInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FinPulseApplication : Application() {
    @Inject lateinit var startupInitializer: AppStartupInitializer

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            startupInitializer.run()
        }
    }
}
