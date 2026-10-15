package com.zenith26.fest.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton managing Retrofit client and API service instance.
 * Allows dynamic configuration of the backend host IP address
 * (e.g. 10.0.2.2 for Android Studio Emulator, or 192.168.x.x for physical devices).
 */
object ApiClient {

    private const val PREFS_NAME = "zenith_api_prefs"
    private const val KEY_BASE_URL = "base_url"

    // Live Cloudflare tunnel accessible from Appetize.io, physical phones, and emulators
    const val DEFAULT_EMULATOR_URL = "https://fruit-colleague-brooklyn-threatening.trycloudflare.com/"
    const val LOCAL_EMULATOR_URL = "http://10.0.2.2:8001/"

    private var sharedPreferences: SharedPreferences? = null
    private var currentBaseUrl: String = DEFAULT_EMULATOR_URL

    private var retrofit: Retrofit? = null
    private var apiService: ZenithApiService? = null

    /**
     * Initialize with Application context to persist base URL preferences.
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentBaseUrl = sharedPreferences?.getString(KEY_BASE_URL, DEFAULT_EMULATOR_URL)
            ?: DEFAULT_EMULATOR_URL
        buildRetrofit()
    }

    /**
     * Get current configured base URL.
     */
    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Update backend base URL and rebuild the service.
     */
    fun setBaseUrl(newUrl: String) {
        var formatted = newUrl.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (!formatted.endsWith("/")) {
            formatted = "$formatted/"
        }
        currentBaseUrl = formatted
        sharedPreferences?.edit()?.putString(KEY_BASE_URL, formatted)?.apply()
        buildRetrofit()
    }

    /**
     * Build or rebuild Retrofit instance.
     */
    private fun buildRetrofit() {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit?.create(ZenithApiService::class.java)
    }

    /**
     * Return active ZenithApiService instance.
     */
    val service: ZenithApiService
        get() {
            if (apiService == null) {
                buildRetrofit()
            }
            return apiService ?: throw IllegalStateException("ApiClient not initialized")
        }
}
