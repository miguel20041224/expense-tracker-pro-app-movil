package com.finpulse.util

import android.content.Context
import com.finpulse.R
import com.finpulse.data.local.prefs.AppPreferences
import com.finpulse.domain.finance.IntelligenceTexts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStrings @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: AppPreferences,
) {
    suspend fun intelligenceTexts(): IntelligenceTexts {
        val locale = preferences.currentLocale()
        val ctx = LocaleHelper.wrapContext(context, locale)
        return IntelligenceTexts(
            healthTitle = ctx.getString(R.string.insight_health_title),
            healthBody = { score -> ctx.getString(R.string.insight_health_body, score) },
            overspendInsightTitle = ctx.getString(R.string.insight_overspend_title),
            overspendInsightBody = ctx.getString(R.string.insight_overspend_body),
            overspendAlertTitle = ctx.getString(R.string.alert_overspend_title),
            overspendAlertMessage = ctx.getString(R.string.alert_overspend_message),
            savingsGoodTitle = ctx.getString(R.string.insight_savings_good_title),
            savingsGoodBody = { rate -> ctx.getString(R.string.insight_savings_good_body, rate) },
            savingsLowTitle = ctx.getString(R.string.insight_savings_low_title),
            savingsLowBody = { rate -> ctx.getString(R.string.insight_savings_low_body, rate) },
            categoryDominanceTitle = { cat -> ctx.getString(R.string.insight_category_title, cat) },
            categoryDominanceBody = { cat, share -> ctx.getString(R.string.insight_category_body, share, cat) },
            debtRiskTitle = ctx.getString(R.string.insight_debt_risk_title),
            debtRiskBody = ctx.getString(R.string.insight_debt_risk_body),
            debtDueTitle = { name -> ctx.getString(R.string.alert_debt_due_title, name) },
            debtDueMessage = { name -> ctx.getString(R.string.alert_debt_due_message, name) },
            snowballTitle = ctx.getString(R.string.insight_snowball_title),
            snowballBody = { name, months -> ctx.getString(R.string.insight_snowball_body, name, months) },
            categoryOther = ctx.getString(R.string.category_other),
        )
    }
}
