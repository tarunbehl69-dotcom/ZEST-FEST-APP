package com.zenith26.fest.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

/**
 * Standard error response structure returned by FastAPI.
 * Typically { "detail": "..." }
 */
data class ApiError(
    @SerializedName("detail")
    val detail: Any? = null
) {
    /**
     * Human-readable detail message string.
     */
    val message: String
        get() = when (detail) {
            is String -> detail
            is List<*> -> detail.joinToString("\n") { it.toString() }
            null -> "An unknown error occurred"
            else -> detail.toString()
        }

    companion object {
        private val gson = Gson()

        fun parse(errorBody: ResponseBody?): String {
            if (errorBody == null) return "An unknown error occurred"
            return try {
                val errorString = errorBody.string()
                val parsed = gson.fromJson(errorString, ApiError::class.java)
                parsed.message.ifBlank { errorString }
            } catch (e: Exception) {
                "Server communication error"
            }
        }
    }
}
