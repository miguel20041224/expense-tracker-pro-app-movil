package com.finpulse.ui.root

import androidx.lifecycle.ViewModel
import com.finpulse.data.local.startup.AppBootstrapState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    bootstrapState: AppBootstrapState,
) : ViewModel() {
    val isReady = bootstrapState.isReady
}
