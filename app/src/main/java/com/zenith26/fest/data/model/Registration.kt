package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

/**
 * Payload sent by the student to register for an event.
 * Matches backend RegistrationIn schema.
 */
data class RegistrationIn(
    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("roll_number")
    val rollNumber: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String? = null
)

/**
 * Confirmation payload returned upon successful registration (HTTP 201).
 * Matches backend RegistrationOut schema.
 */
data class RegistrationOut(
    @SerializedName("id")
    val id: Int,

    @SerializedName("event_id")
    val eventId: Int,

    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("registered_at")
    val registeredAt: String
)

/**
 * Detailed registration record for the admin dashboard.
 * Matches backend RegistrationItemOut schema.
 */
data class RegistrationItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("event_id")
    val eventId: Int,

    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("roll_number")
    val rollNumber: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("registered_at")
    val registeredAt: String,

    @SerializedName("event_title")
    val eventTitle: String
)
