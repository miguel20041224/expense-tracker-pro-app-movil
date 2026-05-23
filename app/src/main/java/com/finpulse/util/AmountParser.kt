package com.finpulse.util

object AmountParser {
    fun parseToMinor(input: String): Long? {
        val normalized = input.trim().replace(" ", "")
        if (normalized.isEmpty()) return null

        val value = parseAmountDouble(normalized) ?: return null
        if (value < 0) return null
        return (value * 100).toLong()
    }

    private fun parseAmountDouble(normalized: String): Double? {
        // Formato europeo: 2.000 = 2000, 1.234,56 = 1234.56
        if (normalized.matches(Regex("""\d{1,3}(\.\d{3})+"""))) {
            return normalized.replace(".", "").toDoubleOrNull()
        }
        if (normalized.contains(',') && normalized.contains('.')) {
            return normalized.replace(".", "").replace(",", ".").toDoubleOrNull()
        }
        if (normalized.contains(',') && !normalized.contains('.')) {
            return normalized.replace(",", ".").toDoubleOrNull()
        }
        return normalized.replace(",", ".").toDoubleOrNull()
    }

    fun formatInputFromMinor(minor: Long): String =
        "%.2f".format(minor / 100.0)
}
