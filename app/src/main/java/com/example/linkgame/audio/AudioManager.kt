package com.example.linkgame.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import com.example.linkgame.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AudioManager {
    @SuppressLint("StaticFieldLeak")
    private lateinit var appContext: Context
    private var bgmPlayer: MediaPlayer? = null
    private var isBgmEnabled = true
    private var isSoundEnabled = true

    fun init(context: Context) {
        appContext = context.applicationContext
        // 读取存储的设置
        CoroutineScope(Dispatchers.IO).launch {
            val bgmFlow = com.example.linkgame.data.repository.SettingsRepository.isBgmEnabled(appContext)
            bgmFlow.collect { enabled ->
                isBgmEnabled = enabled
                if (enabled) startBgm() else stopBgm()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val soundFlow = com.example.linkgame.data.repository.SettingsRepository.isSoundEnabled(appContext)
            soundFlow.collect { enabled ->
                isSoundEnabled = enabled
            }
        }
    }

    fun setBgmEnabled(enabled: Boolean) {
        isBgmEnabled = enabled
        if (enabled) startBgm() else stopBgm()
    }

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun playClick() {
        if (!isSoundEnabled) return
        playShortSound(R.raw.click)
    }

    fun playEliminate() {
        if (!isSoundEnabled) return
        playShortSound(R.raw.eliminate)
    }

    private fun playShortSound(rawId: Int) {
        if (!::appContext.isInitialized) return
        try {
            val mp = MediaPlayer.create(appContext, rawId)
            mp.setOnCompletionListener { it.release() }
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startBgm() {
        if (!isBgmEnabled) return
        if (bgmPlayer == null) {
            if (!::appContext.isInitialized) return
            bgmPlayer = MediaPlayer.create(appContext, R.raw.bgm).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }
        bgmPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun stopBgm() {
        bgmPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun release() {
        bgmPlayer?.release()
        bgmPlayer = null
    }
}