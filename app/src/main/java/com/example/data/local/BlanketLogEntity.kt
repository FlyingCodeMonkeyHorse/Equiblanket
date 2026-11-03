package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blanket_logs")
data class BlanketLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val horseId: Long,
    val horseName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eveningLowTempF: Double,
    val weatherDescription: String,
    val weatherCode: Int,
    val precipitationInches: Double,
    val windSpeedMph: Double,
    val recommendedBlanket: String,
    val actualBlanketUsed: String,
    val comfortFeedback: String, // e.g. "Warm & Happy", "Chilly", "Too Warm / Sweat", "Left Naked"
    val notes: String = ""
)
