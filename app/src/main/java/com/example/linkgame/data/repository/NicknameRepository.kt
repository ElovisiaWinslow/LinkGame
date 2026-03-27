package com.example.linkgame.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.nicknameDataStore by preferencesDataStore(name = "user_prefs")
private val nicknameKey = stringPreferencesKey("user_nickname")

object NicknameRepository {
    suspend fun getNickname(context: Context): String? {
        val prefs = context.nicknameDataStore.data.first()
        return prefs[nicknameKey]
    }

    suspend fun saveNickname(context: Context, nickname: String) {
        context.nicknameDataStore.edit { it[nicknameKey] = nickname }
    }
}