package com.example.linkgame.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun NicknameDialog(
    currentNickname: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var input by remember { mutableStateOf(currentNickname ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置昵称") },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                label = { Text("昵称") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (input.isNotBlank()) {
                        onConfirm(input)
                    }
                }
            ) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun SaveScoreDialog(
    score: Int,
    timeSeconds: Int,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("保存成绩") },
        text = { Text("是否将本次成绩加入排行榜？\n得分：$score\n用时：${timeSeconds}秒") },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("保存并返回")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("不保存")
            }
        }
    )
}