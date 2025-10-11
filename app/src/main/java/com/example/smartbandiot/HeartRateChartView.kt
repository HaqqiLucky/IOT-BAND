package com.example.smartbandiot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class HeartRateChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Data untuk chart (percentage dari maksimal)
    private var chartData = listOf(
        ChartBar("Mar", 55f),  // 55% dari maksimal
        ChartBar("Apr", 72f),  // 72% dari maksimal
        ChartBar("May", 67f),
        ChartBar("Jun", 50f)
    )

    // Paint untuk bar hijau
    private val greenPaint = Paint().apply {
        color = 0xFFCDFF00.toInt() // Warna hijau neon
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Paint untuk bar abu-abu
    private val grayPaint = Paint().apply {
        color = 0xFFE0E0E0.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Paint untuk text
    private val textPaint = Paint().apply {
        color = 0xFF666666.toInt()
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val cornerRadius = 24f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (chartData.isEmpty()) return

        val chartHeight = height - 120f // Sisakan ruang untuk label
        val barWidth = (width / chartData.size.toFloat()) * 0.6f
        val spacing = (width / chartData.size.toFloat()) * 0.4f

        chartData.forEachIndexed { index, bar ->
            val centerX = (width / chartData.size.toFloat()) * index + (width / chartData.size.toFloat()) / 2

            // Hitung tinggi bar
            val greenHeight = (chartHeight * (bar.percentage / 100f))
            val grayHeight = chartHeight - greenHeight

            // Posisi bar
            val left = centerX - barWidth / 2
            val right = centerX + barWidth / 2

            // Gambar bar abu-abu (bagian atas)
            if (grayHeight > 0) {
                val grayRect = RectF(
                    left,
                    50f,
                    right,
                    50f + grayHeight
                )
                canvas.drawRoundRect(grayRect, cornerRadius, cornerRadius, grayPaint)
            }

            // Gambar bar hijau (bagian bawah)
            if (greenHeight > 0) {
                val greenRect = RectF(
                    left,
                    50f + grayHeight,
                    right,
                    50f + chartHeight
                )
                canvas.drawRoundRect(greenRect, cornerRadius, cornerRadius, greenPaint)
            }

            // Gambar label bulan
            canvas.drawText(
                bar.label,
                centerX,
                height - 40f,
                textPaint
            )
        }
    }

    // Fungsi untuk update data chart
    fun setData(newData: List<ChartBar>) {
        chartData = newData
        invalidate() // Refresh tampilan
    }

    // Data class untuk chart bar
    data class ChartBar(
        val label: String,
        val percentage: Float  // 0-100
    )
}