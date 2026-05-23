package com.finpulse.ui.feature.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.domain.repository.AuthRepository
import com.finpulse.util.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val locale = preferences.appLocale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LocaleHelper.LOCALE_ES)

    fun setLocale(tag: String, activity: Activity) {
        viewModelScope.launch {
            preferences.setAppLocale(tag)
            LocaleHelper.apply(tag)
            activity.recreate()
        }
    }

    fun logout(activity: Activity) {
        viewModelScope.launch {
            authRepository.logout()
            activity.recreate()
        }
    }
}
