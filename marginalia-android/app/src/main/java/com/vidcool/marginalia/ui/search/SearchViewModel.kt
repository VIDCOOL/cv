package com.vidcool.marginalia.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vidcool.marginalia.data.MarginaliaApi
import com.vidcool.marginalia.data.MarginaliaApiException
import com.vidcool.marginalia.data.PreferencesRepository
import com.vidcool.marginalia.data.SearchUiState
import com.vidcool.marginalia.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val api = MarginaliaApi()
    private val preferencesRepository = PreferencesRepository(application)

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentPage = 1

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun search(page: Int = 1) {
        val trimmed = _query.value.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = SearchUiState.Error("Enter a search query.")
            return
        }

        currentPage = page
        _uiState.value = SearchUiState.Loading

        viewModelScope.launch {
            val prefs = preferences.value
            try {
                val response = withContext(Dispatchers.IO) {
                    api.search(
                        apiKey = prefs.apiKey,
                        query = trimmed,
                        count = prefs.resultCount,
                        page = page,
                        nsfw = if (prefs.reduceNsfw) 1 else 0,
                    )
                }
                _uiState.value = SearchUiState.Success(
                    query = trimmed,
                    results = response.results,
                    page = page,
                    totalPages = response.pages.coerceAtLeast(1),
                )
            } catch (e: MarginaliaApiException) {
                _uiState.value = SearchUiState.Error(e.message ?: "Search failed.")
            } catch (_: Exception) {
                _uiState.value = SearchUiState.Error(
                    "Could not reach Marginalia. Check your connection.",
                )
            }
        }
    }

    fun nextPage() {
        val state = _uiState.value
        if (state is SearchUiState.Success && state.page < state.totalPages) {
            search(state.page + 1)
        }
    }

    fun previousPage() {
        val state = _uiState.value
        if (state is SearchUiState.Success && state.page > 1) {
            search(state.page - 1)
        }
    }

    fun websiteUrl(): String = api.websiteSearchUrl(_query.value.trim())

    fun applySharedQuery(sharedText: String) {
        _query.value = sharedText.trim()
        if (_query.value.isNotEmpty()) {
            search()
        }
    }

    fun updateApiKey(value: String) {
        viewModelScope.launch { preferencesRepository.setApiKey(value) }
    }

    fun updateResultCount(value: Int) {
        viewModelScope.launch { preferencesRepository.setResultCount(value) }
    }

    fun updateReduceNsfw(value: Boolean) {
        viewModelScope.launch { preferencesRepository.setReduceNsfw(value) }
    }

    fun updateUseWebsiteFallback(value: Boolean) {
        viewModelScope.launch { preferencesRepository.setUseWebsiteFallback(value) }
    }
}
