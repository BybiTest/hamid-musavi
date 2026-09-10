package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // Recommended model for text tasks per system skills
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun generateContent(prompt: String, customApiKey: String = ""): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Priority: Custom key from user in Settings -> BuildConfig injected key
                val apiKey = customApiKey.trim().ifBlank {
                    try {
                        BuildConfig.GEMINI_API_KEY
                    } catch (e: Exception) {
                        ""
                    }
                }

                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    return@withContext Result.failure(
                        IllegalStateException("کلید API هوش مصنوعی تنظیم نشده است. می‌توانید کلید رایگان گوگل را در بخش «تنظیمات برنامه» وارد کنید.")
                    )
                }

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                val partObj = JSONObject().apply {
                                    put("text", prompt)
                                }
                                put(partObj)
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 1200)
                    }
                    put("generationConfig", genConfig)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val url = "$BASE_URL?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorMsg = try {
                        val errObj = JSONObject(responseString).optJSONObject("error")
                        errObj?.optString("message") ?: "خطا در ارتباط با هوش مصنوعی (کد ${response.code})"
                    } catch (e: Exception) {
                        "خطا در ارتباط با سرور هوش مصنوعی (کد ${response.code})"
                    }
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val jsonResponse = JSONObject(responseString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        return@withContext Result.success(text.trim())
                    }
                }

                Result.failure(Exception("پاسخی از هوش مصنوعی دریافت نشد."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
