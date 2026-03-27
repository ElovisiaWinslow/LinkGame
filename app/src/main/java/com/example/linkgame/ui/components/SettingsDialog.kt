package com.example.linkgame.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(
    bgmEnabled: Boolean,
    soundEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (bgm: Boolean, sound: Boolean) -> Unit
) {
    var bgm by remember { mutableStateOf(bgmEnabled) }
    var sound by remember { mutableStateOf(soundEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("背景音乐", modifier = Modifier.weight(1f))
                    Switch(checked = bgm, onCheckedChange = { bgm = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("音效（点击/消除）", modifier = Modifier.weight(1f))
                    Switch(checked = sound, onCheckedChange = { sound = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(bgm, sound) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}