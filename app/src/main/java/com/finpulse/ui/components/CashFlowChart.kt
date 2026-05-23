package com.finpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finpulse.R
import com.finpulse.domain.model.MonthlyTrendPoint
import com.finpulse.ui.theme.FinEmerald
import com.finpulse.ui.theme.FinRose

@Composable
fun CashFlowChart(
    trend: List<MonthlyTrendPoint>,
    modifier: Modifier = Modifier,
) {
    if (trend.isEmpty()) {
        Text(
            stringResource(R.string.chart_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        return
    }
    val maxVal = trend.maxOf { maxOf(it.incomeMinor, it.expenseMinor) }.coerceAtLeast(1L).toFloat()
    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val barGroupWidth = size.width / (trend.size * 2f)
        val barWidth = barGroupWidth * 0.35f
        trend.forEachIndexed { index, point ->
            val x = index * barGroupWidth * 2 + barGroupWidth * 0.25f
            val incomeH = (point.incomeMinor / maxVal) * size.height * 0.85f
            val expenseH = (point.expenseMinor / maxVal) * size.height * 0.85f
            drawRoundRect(
                color = FinEmerald,
                topLeft = Offset(x, size.height - incomeH),
                size = Size(barWidth, incomeH),
                cornerRadius = CornerRadius(6f, 6f),
            )
            drawRoundRect(
                color = FinRose,
                topLeft = Offset(x + barWidth + 4f, size.height - expenseH),
                size = Size(barWidth, expenseH),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
    }
}
