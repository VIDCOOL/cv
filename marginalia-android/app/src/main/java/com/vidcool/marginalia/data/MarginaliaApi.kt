package com.vidcool.marginalia.data

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class MarginaliaApi(
    private val client: OkHttpClient = defaultClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val baseUrl: String = "https://api2.marginalia-search.com",
) {
    @Throws(MarginaliaApiException::class, IOException::class)
    fun search(
        apiKey: String,
        query: String,
        count: Int,
        page: Int,
        nsfw: Int,
    ): SearchResponse {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("search")
            .addQueryParameter("query", query)
            .addQueryParameter("count", count.coerceIn(1, 100).toString())
            .addQueryParameter("page", page.coerceAtLeast(1).toString())
            .addQueryParameter("nsfw", nsfw.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .header("API-Key", apiKey.ifBlank { UserPreferences.DEFAULT_API_KEY })
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            when (response.code) {
                200 -> return json.decodeFromString(SearchResponse.serializer(), body)
                401 -> throw MarginaliaApiException("Invalid API key. Update it in Settings.")
                429 -> throw MarginaliaApiException(
                    "API rate limit reached. Try again later or open the website.",
                )
                in 500..599 -> throw MarginaliaApiException("Marginalia server error (${response.code}).")
                else -> throw MarginaliaApiException(
                    body.ifBlank { "Unexpected response (${response.code})." },
                )
            }
        }
    }

    fun websiteSearchUrl(query: String): String {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8)
        return "https://marginalia-search.com/search?q=$encoded"
    }

    companion object {
        fun defaultClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }
}

class MarginaliaApiException(message: String) : Exception(message)
