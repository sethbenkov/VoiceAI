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

class AssistantService(private val context: Context) {
    private val TAG = "AssistantService"
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // TODO: Move to secure storage
    private var apiKey: String = ""

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
} 