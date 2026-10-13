package com.vidcool.marginalia.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "marginalia_prefs")

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val apiKey = stringPreferencesKey("api_key")
        val resultCount = intPreferencesKey("result_count")
        val reduceNsfw = booleanPreferencesKey("reduce_nsfw")
        val useWebsiteFallback = booleanPreferencesKey("use_website_fallback")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            apiKey = prefs[Keys.apiKey] ?: UserPreferences.DEFAULT_API_KEY,
            resultCount = prefs[Keys.resultCount] ?: 20,
            reduceNsfw = prefs[Keys.reduceNsfw] ?: false,
            useWebsiteFallback = prefs[Keys.useWebsiteFallback] ?: true,
        )
    }

    suspend fun setApiKey(value: String) {
        context.dataStore.edit { it[Keys.apiKey] = value.trim() }
    }

    suspend fun setResultCount(value: Int) {
        context.dataStore.edit { it[Keys.resultCount] = value.coerceIn(5, 50) }
    }

    suspend fun setReduceNsfw(value: Boolean) {
        context.dataStore.edit { it[Keys.reduceNsfw] = value }
    }

    suspend fun setUseWebsiteFallback(value: Boolean) {
        context.dataStore.edit { it[Keys.useWebsiteFallback] = value }
    }
}
