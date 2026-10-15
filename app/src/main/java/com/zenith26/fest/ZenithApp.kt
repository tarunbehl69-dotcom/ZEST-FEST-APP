package com.zenith26.fest

import android.app.Application
import com.zenith26.fest.data.remote.ApiClient

/**
 * Zenith '26 Fest System Application.
 * Initializes base network configuration for local backend connectivity.
 */
class ZenithApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize API client with application context for SharedPreferences persistence
        ApiClient.init(this)
    }
}
