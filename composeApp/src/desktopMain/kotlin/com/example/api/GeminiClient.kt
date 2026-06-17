package com.example.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun consultAdvisor(userMessage: String, systemPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = System.getenv("GEMINI_API_KEY") ?: ""

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            System.err.println("[$TAG] Gemini API key is empty or placeholder!")
            return@withContext "⚠️ Witchers require preparation. The Gemini API key is missing!\n\nTo consult Vesemir, set the `GEMINI_API_KEY` environment variable to your key, then relaunch the app."
        }

        try {
            // Build request JSON
            val requestBodyJson = JSONObject()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", userMessage)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            // Inject system instructions if available
            if (systemPrompt.isNotEmpty()) {
                val systemInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemPrompt)
                sysPartsArray.put(sysPartObj)
                systemInstructionObj.put("parts", sysPartsArray)
                requestBodyJson.put("systemInstruction", systemInstructionObj)
            }

            // Generation config with low/med temperature for lore correctness
            val configObj = JSONObject()
            configObj.put("temperature", 0.7)
            requestBodyJson.put("generationConfig", configObj)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .header("Content-Type", "application/json")
                .build()

            var response = client.newCall(request).execute()
            var retries = 0
            while (retries < 2 && response.code == 503) {
                response.close()
                delay(1000L * (retries + 1))
                response = client.newCall(request).execute()
                retries++
            }
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                System.err.println("[$TAG] Unsuccessful response from Gemini: Code ${response.code}, Body: $responseBodyString")
                return@withContext "🛡️ *The link broke...* (API Error: ${response.code}). Check if your internet is working or your Gemini API Key is active."
            }

            val jsonResponse = JSONObject(responseBodyString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext "🐺 Dandelion seems to have lost his tongue. No response candidate returned from the Path."
            }

            val content = candidates.getJSONObject(0).optJSONObject("content")
            if (content == null) {
                return@withContext "🐺 Dandelion did not respond. Content is missing."
            }

            val parts = content.optJSONArray("parts")
            if (parts == null || parts.length() == 0) {
                return@withContext "🐺 Dandelion returned an empty scroll of parchment."
            }

            val text = parts.getJSONObject(0).optString("text")
            return@withContext text ?: "Empty parchment."

        } catch (e: Exception) {
            System.err.println("[$TAG] Error consulting advisor: ${e.message}")
            return@withContext "☠️ *A foglet disrupted your connection.* Error: ${e.localizedMessage}"
        }
    }
}
