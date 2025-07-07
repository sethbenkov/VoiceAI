package com.sethbenkov.voiceai.services

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Binder
import android.os.IBinder
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.sethbenkov.voiceai.database.AppDatabase
import com.sethbenkov.voiceai.database.Usage

/**
 * AssistantService is now a Foreground Service that can be bound to from activities.
 * It provides a Binder for clients to call getResponse and other methods.
 */
class AssistantService : Service() {
    private val TAG = "AssistantService"
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private var apiKey: String = ""

    // Binder given to clients
    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): AssistantService = this@AssistantService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        loadApiKey()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceWithNotification()
        return START_STICKY
    }

    /**
     * Creates a persistent notification and starts the service in the foreground.
     */
    private fun startForegroundServiceWithNotification() {
        val channelId = "assistant_service_channel"
        val channelName = "VoiceAI Assistant Service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("VoiceAI Assistant Running")
            .setContentText("Listening for commands in the background.")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    data class ChatRequest(
        val model: String = "gpt-3.5-turbo",
        val messages: List<Message>,
        val temperature: Double = 0.7
    )

    data class Message(
        val role: String,
        val content: String
    )

    data class ChatResponse(
        val id: String,
        val choices: List<Choice>,
        val usage: Usage
    )

    data class Choice(
        val message: Message,
        @SerializedName("finish_reason")
        val finishReason: String
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int
    )

    // Add a coroutine scope for database operations
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getResponse(userInput: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messages = listOf(
                Message("system", "You are a helpful assistant."),
                Message("user", userInput)
            )

            val requestBody = ChatRequest(messages = messages)
            val jsonBody = gson.toJson(requestBody)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(jsonBody.toRequestBody(JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "API call failed: ${response.code}")
                    return@withContext Result.failure(IOException("API call failed: ${response.code}"))
                }

                val responseBody = response.body?.string()
                if (responseBody == null) {
                    Log.e(TAG, "Empty response body")
                    return@withContext Result.failure(IOException("Empty response body"))
                }

                val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
                val assistantMessage = chatResponse.choices.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(IOException("No response from assistant"))

                Log.d(TAG, "Token usage: ${chatResponse.usage}")
                // Save token usage to Room database
                val usage = Usage(
                    promptTokens = chatResponse.usage.promptTokens,
                    completionTokens = chatResponse.usage.completionTokens,
                    totalTokens = chatResponse.usage.totalTokens
                )
                serviceScope.launch {
                    AppDatabase.getDatabase(this@AssistantService).usageDao().insert(usage)
                }
                Result.success(assistantMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting response", e)
            Result.failure(e)
        }
    }

    fun setApiKey(key: String) {
        apiKey = key
    }

    /**
     * Loads the API key from EncryptedSharedPreferences.
     * Call this when the service is created and when the key is updated.
     */
    fun loadApiKey() {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val encryptedPrefs = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        apiKey = encryptedPrefs.getString("api_key", "") ?: ""
    }
} 