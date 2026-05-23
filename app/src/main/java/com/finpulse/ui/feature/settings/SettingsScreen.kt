package com.finpulse.ui.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpulse.R
import com.finpulse.ui.components.DetailScreenContent
import com.finpulse.ui.components.FinPulseDetailScaffold
import com.finpulse.util.LocaleHelper

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val locale by viewModel.locale.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    FinPulseDetailScaffold(title = stringResource(R.string.nav_settings), onBack = onBack) { padding ->
        DetailScreenContent(padding) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = locale == LocaleHelper.LOCALE_ES,
                        onClick = { viewModel.setLocale(LocaleHelper.LOCALE_ES, activity) },
                        label = { Text(stringResource(R.string.language_spanish)) },
                    )
                    FilterChip(
                        selected = locale == LocaleHelper.LOCALE_EN,
                        onClick = { viewModel.setLocale(LocaleHelper.LOCALE_EN, activity) },
                        label = { Text(stringResource(R.string.language_english)) },
                    )
                }
                Button(
                    onClick = { viewModel.logout(activity) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.auth_logout))
                }
            }
        }
    }
}
