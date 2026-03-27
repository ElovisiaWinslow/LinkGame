package com.example.linkgame.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsRepository {
    private val BGM_ENABLED_KEY = booleanPreferencesKey("bgm_enabled")
    private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")

    // 默认值：背景音乐开启，音效开启
    fun isBgmEnabled(context: Context): Flow<Boolean> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[BGM_ENABLED_KEY] ?: true
        }
    }

    fun isSoundEnabled(context: Context): Flow<Boolean> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[SOUND_ENABLED_KEY] ?: true
        }
    }

    suspend fun setBgmEnabled(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[BGM_ENABLED_KEY] = enabled
        }
    }

    suspend fun setSoundEnabled(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled
        }
    }
}