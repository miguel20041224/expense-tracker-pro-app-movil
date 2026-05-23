package com.finpulse.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormat {
    fun formatMinor(amountMinor: Long, currencyCode: String = "USD", locale: Locale = Locale.getDefault()): String {
        val amount = amountMinor / 100.0
        val format = NumberFormat.getCurrencyInstance(locale)
        runCatching { format.currency = Currency.getInstance(currencyCode) }
        return format.format(amount)
    }
}
