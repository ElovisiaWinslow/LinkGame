package com.example.linkgame.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import java.util.UUID

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LeaderboardEntry(
    val id: String = UUID.randomUUID().toString(),
    val nickname: String,
    val score: Int,
    val timeSeconds: Int,
    val mode: String,          // "challenge" or "endless"
    val difficulty: String? = null, // 仅无尽模式有效
    val date: Long = System.currentTimeMillis()
)