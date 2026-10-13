package com.vidcool.marginalia.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val results: List<SearchResult> = emptyList(),
    val pages: Int = 1,
)

@Serializable
data class SearchResult(
    val url: String,
    val title: String = "",
    val description: String = "",
)

sealed interface SearchUiState {
    data object Idle : SearchUiState

    data object Loading : SearchUiState

    data class Success(
        val query: String,
        val results: List<SearchResult>,
        val page: Int,
        val totalPages: Int,
    ) : SearchUiState

    data class Error(
        val message: String,
        val canOpenWebsite: Boolean = true,
    ) : SearchUiState
}

data class UserPreferences(
    val apiKey: String = DEFAULT_API_KEY,
    val resultCount: Int = 20,
    val reduceNsfw: Boolean = false,
    val useWebsiteFallback: Boolean = true,
) {
    companion object {
        const val DEFAULT_API_KEY = "public"
    }
}
