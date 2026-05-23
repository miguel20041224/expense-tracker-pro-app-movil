package com.finpulse.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {
    const val LOCALE_ES = "es"
    const val LOCALE_EN = "en"

    fun apply(localeTag: String) {
        val locales = LocaleListCompat.forLanguageTags(localeTag)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun wrapContext(base: Context, localeTag: String): Context {
        val locale = Locale.forLanguageTag(localeTag)
        val config = base.resources.configuration
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }
}
