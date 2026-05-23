package com.finpulse.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.data.local.startup.AppBootstrapState
import com.finpulse.data.local.startup.AppStartupInitializer
import com.finpulse.data.session.CurrentUserProvider
import com.finpulse.domain.repository.AuthRepository
import com.finpulse.util.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    private val bootstrapState: AppBootstrapState,
    private val authRepository: AuthRepository,
    private val preferences: AppPreferences,
    private val currentUser: CurrentUserProvider,
    private val startupInitializer: AppStartupInitializer,
) : ViewModel() {
    val isReady = bootstrapState.isReady
    val loggedInUserId = authRepository.observeLoggedInUserId()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            LocaleHelper.apply(preferences.appLocale.first())
            val restored = currentUser.restoreFromPreferences()
            if (restored != null) {
                startupInitializer.run(restored)
            } else {
                bootstrapState.markReady()
            }
        }
    }

    fun onLoggedIn(userId: Long) {
        viewModelScope.launch {
            bootstrapState.reset()
            startupInitializer.run(userId)
        }
    }
}
