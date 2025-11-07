package com.example.smartbandiot

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class ChallengeItemData(
    val title: String,
    val timeInSec: Int,
    val distanceKm: Double = GlobalData.distanceTargetNextSession,
    val date: Long = System.currentTimeMillis()
) {
    val pace: Double
        get() = (timeInSec / 60.0) / distanceKm

    val formattedPace: String
        get() {
            val minutes = pace.toInt()
            val seconds = ((pace - minutes) * 60).roundToInt()
            return String.format("%d'%02d''", minutes, seconds)
        }

    val formattedTime: String
        get() {
            val hours = timeInSec / 3600
            val minutes = (timeInSec % 3600) / 60
            return String.format("%02d:%02d", hours, minutes)
        }

    val formattedDate: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        }
}
