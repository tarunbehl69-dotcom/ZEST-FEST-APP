package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for a fest event.
 * Matches backend EventOut and EventDetail Pydantic models.
 */
data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("event_date")
    val eventDate: String,

    @SerializedName("event_time")
    val eventTime: String,

    @SerializedName("venue")
    val venue: String,

    @SerializedName("capacity")
    val capacity: Int,

    @SerializedName("fee")
    val fee: Int = 0,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("available_seats")
    val availableSeats: Int
) {
    /**
     * Check if event has reached maximum capacity.
     */
    val isFull: Boolean
        get() = availableSeats <= 0

    /**
     * Compute registered seats count (capacity - availableSeats).
     */
    val registeredCount: Int
        get() = (capacity - availableSeats).coerceAtLeast(0)

    /**
     * Compute capacity utilization percentage.
     */
    val utilizationPct: Double
        get() = if (capacity > 0) {
            val pct = (registeredCount.toDouble() / capacity.toDouble()) * 100.0
            Math.round(pct * 10.0) / 10.0
        } else 0.0

    /**
     * Formatted fee string ("Free" or "₹<amount>").
     */
    val formattedFee: String
        get() = if (fee == 0) "Free" else "₹$fee"
}
