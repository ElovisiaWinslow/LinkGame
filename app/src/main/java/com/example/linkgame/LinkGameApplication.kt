package com.example.linkgame

import android.app.Application
import com.example.linkgame.audio.AudioManager

class LinkGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AudioManager.init(this)
    }
}