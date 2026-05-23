package com.finpulse.ui.feature.reports

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.model.DashboardSnapshot
import com.finpulse.domain.usecase.ObserveDashboardUseCase
import com.finpulse.util.MoneyFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    observeDashboard: ObserveDashboardUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    val snapshot = observeDashboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var lastPdfPath: String? = null
        private set

    fun exportPdf(snapshot: DashboardSnapshot, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                runCatching { buildPdf(snapshot) }.getOrNull()
            }
            lastPdfPath = path
            onResult(path)
        }
    }

    private fun buildPdf(snapshot: DashboardSnapshot): String {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply { textSize = 14f }
        var y = 40f
        fun line(text: String) {
            canvas.drawText(text, 40f, y, paint)
            y += 22f
        }
        line(context.getString(com.finpulse.R.string.pdf_report_title))
        line(context.getString(com.finpulse.R.string.pdf_balance, MoneyFormat.formatMinor(snapshot.balanceMinor)))
        line(context.getString(com.finpulse.R.string.pdf_income, MoneyFormat.formatMinor(snapshot.incomeMinor)))
        line(context.getString(com.finpulse.R.string.pdf_expenses, MoneyFormat.formatMinor(snapshot.expenseMinor)))
        line(context.getString(com.finpulse.R.string.pdf_health, snapshot.healthScore))
        line(context.getString(com.finpulse.R.string.pdf_debt, MoneyFormat.formatMinor(snapshot.totalDebtMinor)))
        snapshot.insights.take(5).forEach { line("• ${it.title}: ${it.body}") }
        doc.finishPage(page)
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val file = File(dir, "finpulse_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file.absolutePath
    }
}
