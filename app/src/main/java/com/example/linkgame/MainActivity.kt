package com.example.linkgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.linkgame.audio.AudioManager
import com.example.linkgame.ui.navigation.GameNavHost
import com.example.linkgame.ui.theme.LinkGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkGameTheme {
                GameNavHost(onExit = { exitApp() })
            }
        }
    }

    private fun exitApp() {
        // 释放音频资源
        AudioManager.release()
        // 结束所有 Activity
        finishAffinity()
        // 强制终止进程（可选，但确保彻底退出）
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}